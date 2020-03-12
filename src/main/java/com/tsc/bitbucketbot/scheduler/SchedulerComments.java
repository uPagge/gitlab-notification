package com.tsc.bitbucketbot.scheduler;

import com.tsc.bitbucketbot.config.BitbucketConfig;
import com.tsc.bitbucketbot.domain.MessageSend;
import com.tsc.bitbucketbot.domain.Pagination;
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
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Scheduled(cron = "0 5 8-18 * * MON-FRI")
    public void test() {
        long newLastCommentId = commentService.getLastCommentId();
        long commentId = newLastCommentId + 1;
        long count = 0;
        do {
            int page = 0;
            Page<PullRequest> pageRequestSheet = pullRequestsService.getAll(Pagination.of(page++, COUNT));
            while (pageRequestSheet.hasContent()) {
                boolean commentSearch = false;
                for (PullRequest pullRequest : pageRequestSheet.getContent()) {
                    final Optional<CommentJson> commentJson = Utils.urlToJson(
                            getPrUrl(commentId, pullRequest),
                            bitbucketConfig.getToken(),
                            CommentJson.class
                    );
                    if (commentJson.isPresent()) {
                        commentSearch = true;
                        final CommentJson comment = commentJson.get();
                        notification(
                                comment,
                                pullRequest.getName(),
                                bitbucketConfig.getUrlPullRequest()
                                        .replace("{projectKey}", pullRequest.getProjectKey())
                                        .replace("{repositorySlug}", pullRequest.getRepositorySlug())
                                        .replace("{pullRequestId}", pullRequest.getBitbucketId().toString())
                        );
                        newLastCommentId = commentId;
                        break;
                    }
                }
                if (commentSearch) {
                    count = 0;
                    break;
                } else {
                    count++;
                }
                pageRequestSheet = pullRequestsService.getAll(Pagination.of(page++, COUNT));
            }
            commentId += 1;
        } while (count < NO_COMMENT);
        commentService.saveLastCommentId(newLastCommentId);
    }

    private String getPrUrl(long lastCommentId, PullRequest pullRequest) {
        return bitbucketConfig.getUrlPullRequestComment()
                .replace("{projectKey}", pullRequest.getProjectKey())
                .replace("{repositorySlug}", pullRequest.getRepositorySlug())
                .replace("{pullRequestId}", pullRequest.getBitbucketId().toString())
                .replace("{commentId}", String.valueOf(lastCommentId));
    }

    private void notification(@NonNull CommentJson comment, @NonNull String namePr, @NonNull String urlPr) {
        final String message = comment.getText();
        Matcher matcher = PATTERN.matcher(message);
        while (matcher.find()) {
            final String login = matcher.group(0).replace("@", "");
            userService.getTelegramIdByLogin(login).ifPresent(
                    telegramId -> messageSendService.add(
                            MessageSend.builder()
                                    .telegramId(telegramId)
                                    .message(Message.personalNotify(comment, namePr, urlPr))
                                    .build()
                    )
            );
        }
    }

}
