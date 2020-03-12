package com.tsc.bitbucketbot.scheduler;

import com.tsc.bitbucketbot.domain.MessageSend;
import com.tsc.bitbucketbot.domain.ReviewerStatus;
import com.tsc.bitbucketbot.domain.entity.PullRequest;
import com.tsc.bitbucketbot.domain.entity.User;
import com.tsc.bitbucketbot.service.MessageSendService;
import com.tsc.bitbucketbot.service.PullRequestsService;
import com.tsc.bitbucketbot.service.UserService;
import com.tsc.bitbucketbot.utils.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerNotification {

    private final UserService userService;
    private final PullRequestsService pullRequestsService;
    private final MessageSendService messageSendService;

    // Утреннее сообщение
    @Scheduled(cron = "0 15 8 * * MON-FRI")
    public void goodMorning() {
        List<User> allRegister = userService.getAllRegister();
        for (User user : allRegister) {
            List<PullRequest> pullRequestsReviews = pullRequestsService.getAllByReviewerAndStatuses(
                    user.getLogin(),
                    ReviewerStatus.NEEDS_WORK
            );
            List<PullRequest> pullRequestsNeedWork = pullRequestsService.getAllByAuthorAndReviewerStatus(user.getLogin(), ReviewerStatus.UNAPPROVED);
            messageSendService.add(
                    MessageSend.builder()
                            .telegramId(user.getTelegramId())
                            .message(Message.goodMorningStatistic(pullRequestsReviews, pullRequestsNeedWork))
                            .build()
            );
        }
    }

    @Scheduled(cron = "0 0 18 * * FRI")
    public void goodWeekEnd() {
        List<User> allRegister = userService.getAllRegister();
        for (User user : allRegister) {
            messageSendService.add(
                    MessageSend.builder()
                            .telegramId(user.getTelegramId())
                            .message(Message.goodWeekEnd())
                            .build()
            );
        }
    }

}
