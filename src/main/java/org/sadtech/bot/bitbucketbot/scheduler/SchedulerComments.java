package org.sadtech.bot.bitbucketbot.scheduler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.bitbucketbot.config.BitbucketConfig;
import org.sadtech.bot.bitbucketbot.domain.Answer;
import org.sadtech.bot.bitbucketbot.domain.Pagination;
import org.sadtech.bot.bitbucketbot.domain.change.AnswerCommentChange;
import org.sadtech.bot.bitbucketbot.domain.change.CommentChange;
import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.CommentJson;
import org.sadtech.bot.bitbucketbot.service.ChangeService;
import org.sadtech.bot.bitbucketbot.service.CommentService;
import org.sadtech.bot.bitbucketbot.service.PullRequestsService;
import org.sadtech.bot.bitbucketbot.service.UserService;
import org.sadtech.bot.bitbucketbot.service.Utils;
import org.sadtech.bot.bitbucketbot.service.executor.DataScan;
import org.sadtech.bot.bitbucketbot.service.executor.ResultScan;
import org.sadtech.bot.bitbucketbot.service.impl.ExecutorScanner;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerComments {

    private static final Integer COUNT = 100;
    private static final Integer NO_COMMENT = 20;
    private static final Pattern PATTERN = Pattern.compile("@[\\w]+");

    private final CommentService commentService;
    private final PullRequestsService pullRequestsService;
    private final UserService userService;
    private final ChangeService changeService;
    private final ExecutorScanner executorScanner;

    private final BitbucketConfig bitbucketConfig;

    @Scheduled(cron = "0 */3 * * * MON-FRI")
    public void newComments() {
        long commentId = commentService.getLastCommentId() + 1;
        int count = 0;
        do {
            List<DataScan> commentUrls = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                int page = 0;
                Page<PullRequest> pullRequestPage = pullRequestsService.getAll(Pagination.of(page, COUNT));
                while (pullRequestPage.hasContent()) {
                    long finalCommentId = commentId;
                    commentUrls.addAll(pullRequestPage.getContent().stream()
                            .map(pullRequest -> new DataScan(getCommentUrl(finalCommentId, pullRequest), pullRequest.getUrl()))
                            .collect(Collectors.toList()));
                    pullRequestPage = pullRequestsService.getAll(Pagination.of(++page, COUNT));
                }
                commentId++;
            }
            executorScanner.registration(commentUrls);
            final List<ResultScan> result = executorScanner.getResult();
            if (!result.isEmpty()) {
                result.forEach(resultScan -> {
                    notificationPersonal(resultScan.getCommentJson(), resultScan.getUrlPr());
                    saveComments(resultScan.getCommentJson(), resultScan.getUrlComment(), resultScan.getUrlPr());
                });
                count = 0;
            }
        } while (count++ < NO_COMMENT);
    }

    @Scheduled(cron = "0 */1 * * * MON-FRI")
    public void oldComments() {
        @NonNull final List<Comment> comments = commentService.getAllBetweenDate(LocalDateTime.now().minusDays(10), LocalDateTime.now());
        for (Comment comment : comments) {
            final Optional<CommentJson> optCommentJson = Utils.urlToJson(
                    comment.getUrl(),
                    bitbucketConfig.getToken(),
                    CommentJson.class
            );
            if (optCommentJson.isPresent()) {
                final CommentJson commentJson = optCommentJson.get();
                final Set<Long> oldAnswerIds = comment.getAnswers();
                final List<CommentJson> newAnswers = commentJson.getComments().stream()
                        .filter(answerJson -> !oldAnswerIds.contains(answerJson.getId()))
                        .collect(Collectors.toList());
                if (!newAnswers.isEmpty()) {
                    changeService.add(
                            AnswerCommentChange.builder()
                                    .telegramIds(
                                            userService.getTelegramIdByLogin(commentJson.getAuthor().getName())
                                                    .map(Collections::singleton)
                                                    .orElse(Collections.emptySet())
                                    )
                                    .url(comment.getPrUrl())
                                    .youMessage(commentJson.getText())
                                    .answers(
                                            newAnswers.stream()
                                                    .map(json -> Answer.of(json.getAuthor().getName(), json.getText()))
                                                    .collect(Collectors.toList())
                                    )
                                    .build()
                    );
                    comment.getAnswers().addAll(newAnswers.stream().map(CommentJson::getId).collect(Collectors.toList()));
                    commentService.save(comment);
                }
            }
        }
    }

    @NonNull
    private void saveComments(CommentJson comment, String commentUrl, String prUrl) {
        final Comment newComment = new Comment();
        newComment.setId(comment.getId());
        newComment.setDate(LocalDateTime.now());
        newComment.setUrl(commentUrl);
        newComment.setPrUrl(prUrl);
        userService.getTelegramIdByLogin(comment.getAuthor().getName()).ifPresent(newComment::setTelegram);
        commentService.save(newComment);
    }

    private String getCommentUrl(long commentId, PullRequest pullRequest) {
        return bitbucketConfig.getUrlPullRequestComment()
                .replace("{projectKey}", pullRequest.getProjectKey())
                .replace("{repositorySlug}", pullRequest.getRepositorySlug())
                .replace("{pullRequestId}", pullRequest.getBitbucketId().toString())
                .replace("{commentId}", String.valueOf(commentId));
    }

    private void notificationPersonal(@NonNull CommentJson comment, @NonNull String urlPr) {
        Matcher matcher = PATTERN.matcher(comment.getText());
        Set<String> recipientsLogins = new HashSet<>();
        while (matcher.find()) {
            final String login = matcher.group(0).replace("@", "");
            recipientsLogins.add(login);
        }
        final Set<Long> recipientsIds = userService.getAllTelegramIdByLogin(recipientsLogins);
        changeService.add(
                CommentChange.builder()
                        .authorName(comment.getAuthor().getName())
                        .url(urlPr)
                        .telegramIds(recipientsIds)
                        .message(comment.getText())
                        .build()
        );
    }

}
