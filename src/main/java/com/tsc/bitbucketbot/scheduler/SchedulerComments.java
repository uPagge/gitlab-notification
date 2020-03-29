package com.tsc.bitbucketbot.scheduler;

import com.tsc.bitbucketbot.config.BitbucketConfig;
import com.tsc.bitbucketbot.domain.MessageSend;
import com.tsc.bitbucketbot.domain.Pagination;
import com.tsc.bitbucketbot.domain.entity.Comment;
import com.tsc.bitbucketbot.domain.entity.PullRequest;
import com.tsc.bitbucketbot.dto.bitbucket.CommentJson;
import com.tsc.bitbucketbot.service.CommentService;
import com.tsc.bitbucketbot.service.MessageSendService;
import com.tsc.bitbucketbot.service.PullRequestsService;
import com.tsc.bitbucketbot.service.UserService;
import com.tsc.bitbucketbot.service.Utils;
import com.tsc.bitbucketbot.utils.Message;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private static final Integer NO_COMMENT = 100;
    private static final Pattern PATTERN = Pattern.compile("@[\\w]+");

    private final CommentService commentService;
    private final PullRequestsService pullRequestsService;
    private final MessageSendService messageSendService;
    private final UserService userService;

    private final BitbucketConfig bitbucketConfig;

    @Scheduled(cron = "0 */5 8-18 * * MON-FRI")
    public void newComments() {
        log.info("Начало сканирования комментариев");
        long commentId = commentService.getLastCommentId() + 1;
        long count = 0;
        do {
            int page = 0;
            Page<PullRequest> pullRequestPage = pullRequestsService.getAll(Pagination.of(page++, COUNT));
            while (pullRequestPage.hasContent()) {
                for (PullRequest pullRequest : pullRequestPage.getContent()) {
                    final String commentUrl = getCommentUrl(commentId, pullRequest);
                    final Optional<CommentJson> optCommentJson = Utils.urlToJson(commentUrl, bitbucketConfig.getToken(), CommentJson.class);
                    if (optCommentJson.isPresent()) {
                        final CommentJson commentJson = optCommentJson.get();
                        notification(commentJson, pullRequest);
                        saveComments(commentJson, commentUrl);
                        count = 0;
                        break;
                    }
                }
                pullRequestPage = pullRequestsService.getAll(Pagination.of(page++, COUNT));
            }
            count++;
            commentId += 1;
        } while (count < NO_COMMENT);
        log.info("Конец сканирования комментариев");
    }

    @Scheduled(cron = "0 */1 8-18 * * MON-FRI")
    public void oldComments() {
        @NonNull final List<Comment> comments = commentService.getAllBetweenDate(LocalDate.now().minusDays(10), LocalDate.now());
        for (Comment comment : comments) {
            final Optional<CommentJson> optCommentJson = Utils.urlToJson(
                    comment.getUrl(),
                    bitbucketConfig.getToken(),
                    CommentJson.class
            );
            if (optCommentJson.isPresent()) {
                final CommentJson commentJson = optCommentJson.get();
                final Set<Long> oldAnswerIds = comment.getAnswers();
                final List<CommentJson> answerJsons = commentJson.getComments().stream()
                        .filter(answerJson -> !oldAnswerIds.contains(answerJson.getId()))
                        .collect(Collectors.toList());
                if (!answerJsons.isEmpty()) {
                    userService.getTelegramIdByLogin(commentJson.getAuthor().getName()).ifPresent(
                            telegramAuthorComment -> messageSendService.add(
                                    MessageSend.builder()
                                            .telegramId(telegramAuthorComment)
                                            .message(Message.answerComment(commentJson.getText(), answerJsons))
                                            .build()
                            )
                    );
                    comment.getAnswers().addAll(answerJsons.stream().map(CommentJson::getId).collect(Collectors.toList()));
                    commentService.save(comment);
                }
            } else {
                commentService.delete(comment.getId());
            }
        }
    }

    @NonNull
    private void saveComments(CommentJson comment, String commentUrl) {
        final Comment newComment = new Comment();
        newComment.setId(comment.getId());
        newComment.setDate(LocalDate.now());
        newComment.setUrl(commentUrl);
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
//        notificationAuthorPr(comment, pullRequest);
        notificationPersonal(comment, pullRequest);
    }

    private void notificationAuthorPr(@NonNull CommentJson comment, @NonNull PullRequest pullRequest) {
        final Long authorTelegram = pullRequest.getAuthor().getTelegramId();
        if (authorTelegram != null) {
            messageSendService.add(
                    MessageSend.builder()
                            .telegramId(authorTelegram)
                            .message(Message.commentPr(comment, pullRequest.getName(), pullRequest.getUrl()))
                            .build()
            );
        }
    }

    private void notificationPersonal(@NonNull CommentJson comment, @NonNull PullRequest pullRequest) {
        Matcher matcher = PATTERN.matcher(comment.getText());
        while (matcher.find()) {
            final String login = matcher.group(0).replace("@", "");
            userService.getTelegramIdByLogin(login).ifPresent(
                    telegramId -> messageSendService.add(
                            MessageSend.builder()
                                    .telegramId(telegramId)
                                    .message(Message.personalNotify(comment, pullRequest.getName(), pullRequest.getUrl()))
                                    .build()
                    )
            );
        }
    }

}
