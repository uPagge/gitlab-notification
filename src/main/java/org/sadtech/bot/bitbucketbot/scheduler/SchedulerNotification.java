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
import org.sadtech.bot.bitbucketbot.utils.Message;
import org.sadtech.bot.bitbucketbot.utils.Smile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchedulerNotification {

    private static final Set<String> tksLoginNotify = new HashSet<>(Arrays.asList(
            "mstruchkov", "drasskazov", "dganin", "emukhin", "ktorgaeva", "imescheryakov", "kkeglev"
    ));
    private static final Set<PullRequestStatus> statuses = Collections.singleton(PullRequestStatus.OPEN);

    private final PersonService personService;
    private final PullRequestsService pullRequestsService;
    private final MessageSendService messageSendService;

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

    @Scheduled(cron = "0 25 10 * * MON-FRI")
    public void tks() {
        List<Person> usersTks = personService.getAllRegister().stream()
                .filter(user -> tksLoginNotify.contains(user.getLogin()))
                .collect(Collectors.toList());
        for (Person person : usersTks) {
            messageSendService.add(
                    MessageSend.builder()
                            .telegramId(person.getTelegramId())
                            .message("☎️ Скоро созвон" + Smile.HR + "https://meet.google.com/czs-vigu-mte")
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
        }
    }

}
