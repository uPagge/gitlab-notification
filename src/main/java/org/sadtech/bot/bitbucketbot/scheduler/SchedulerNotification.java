package org.sadtech.bot.bitbucketbot.scheduler;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.domain.MessageSend;
import org.sadtech.bot.bitbucketbot.domain.PullRequestStatus;
import org.sadtech.bot.bitbucketbot.domain.ReviewerStatus;
import org.sadtech.bot.bitbucketbot.domain.entity.Person;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.service.MessageSendService;
import org.sadtech.bot.bitbucketbot.service.PersonService;
import org.sadtech.bot.bitbucketbot.service.PullRequestsService;
import org.sadtech.bot.bitbucketbot.service.ReportService;
import org.sadtech.bot.bitbucketbot.utils.Message;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SchedulerNotification {

    private static final Set<PullRequestStatus> statuses = Collections.singleton(PullRequestStatus.OPEN);

    private final PersonService personService;
    private final PullRequestsService pullRequestsService;
    private final MessageSendService messageSendService;
    private final ReportService reportService;

    // Утреннее сообщение
    @Scheduled(cron = "0 15 8 * * MON-FRI")
    public void goodMorning() {
        List<Person> allRegister = personService.getAllRegister();
        for (Person user : allRegister) {
            List<PullRequest> pullRequestsReviews = pullRequestsService.getAllByReviewerAndStatuses(
                    user.getLogin(),
                    ReviewerStatus.NEEDS_WORK,
                    statuses
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
        List<Person> allRegister = personService.getAllRegister();
        for (Person user : allRegister) {
            messageSendService.add(
                    MessageSend.builder()
                            .telegramId(user.getTelegramId())
                            .message(Message.goodWeekEnd())
                            .build()
            );
            reportService.generateReport(user.getLogin());
        }
    }

}
