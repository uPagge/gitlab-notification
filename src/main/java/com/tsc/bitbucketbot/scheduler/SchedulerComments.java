package com.tsc.bitbucketbot.scheduler;

import com.tsc.bitbucketbot.config.BitbucketConfig;
import com.tsc.bitbucketbot.domain.Answer;
import com.tsc.bitbucketbot.domain.Pagination;
import com.tsc.bitbucketbot.domain.change.AnswerCommentChange;
import com.tsc.bitbucketbot.domain.change.CommentChange;
import com.tsc.bitbucketbot.domain.entity.Comment;
import com.tsc.bitbucketbot.domain.entity.PullRequest;
import com.tsc.bitbucketbot.dto.bitbucket.CommentJson;
import com.tsc.bitbucketbot.service.ChangeService;
import com.tsc.bitbucketbot.service.CommentService;
import com.tsc.bitbucketbot.service.PullRequestsService;
import com.tsc.bitbucketbot.service.UserService;
import com.tsc.bitbucketbot.service.Utils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    private static final Integer NO_COMMENT = 30;
    private static final Pattern PATTERN = Pattern.compile("@[\\w]+");

    private final CommentService commentService;
    private final PullRequestsService pullRequestsService;
    private final UserService userService;
    private final ChangeService changeService;

    private final BitbucketConfig bitbucketConfig;

    @Scheduled(cron = "0 */4 8-18 * * MON-FRI")
    public void newComments() {
        log.info("Начало сканирования комментариев");
        long commentId = commentService.getLastCommentId() + 1;
        long count = 0;
        do {
            int page = 0;
            Page<PullRequest> pullRequestPage = pullRequestsService.getAll(Pagination.of(page, COUNT));
            while (pullRequestPage.hasContent()) {
                for (PullRequest pullRequest : pullRequestPage.getContent()) {
                    final String commentUrl = getCommentUrl(commentId, pullRequest);
                    final Optional<CommentJson> optCommentJson = Utils.urlToJson(commentUrl, bitbucketConfig.getToken(), CommentJson.class);
                    if (optCommentJson.isPresent()) {
                        final CommentJson commentJson = optCommentJson.get();
                        notification(commentJson, pullRequest);
                        saveComments(commentJson, commentUrl, pullRequest.getUrl());
                        count = 0;
                        break;
                    }
                }
                pullRequestPage = pullRequestsService.getAll(Pagination.of(++page, COUNT));
            }
            count++;
            commentId += 1;
        } while (count < NO_COMMENT);
        log.info("Конец сканирования комментариев");
    }

    @Scheduled(cron = "0 */1 8-18 * * MON-FRI")
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
                                    .telegramId(userService.getTelegramIdByLogin(commentJson.getAuthor().getName()).orElse(null))
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

    private void notification(@NonNull CommentJson comment, @NonNull PullRequest pullRequest) {
        notificationPersonal(comment, pullRequest);
    }

    private void notificationPersonal(@NonNull CommentJson comment, @NonNull PullRequest pullRequest) {
        Matcher matcher = PATTERN.matcher(comment.getText());
        Set<String> recipientsLogins = new HashSet<>();
        while (matcher.find()) {
            final String login = matcher.group(0).replace("@", "");
            recipientsLogins.add(login);
        }
        final List<Long> recipientsIds = userService.getAllTelegramIdByLogin(recipientsLogins);
        changeService.add(
                CommentChange.builder()
                        .authorName(comment.getAuthor().getName())
                        .name(pullRequest.getName())
                        .url(pullRequest.getUrl())
                        .telegramId(recipientsIds)
                        .message(comment.getText())
                        .build()
        );
    }

}
