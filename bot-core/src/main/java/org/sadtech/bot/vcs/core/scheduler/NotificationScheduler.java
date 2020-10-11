package org.sadtech.bot.vcs.core.scheduler;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.core.config.properties.AppProperty;
import org.sadtech.bot.vcs.core.domain.EntityType;
import org.sadtech.bot.vcs.core.domain.PullRequestStatus;
import org.sadtech.bot.vcs.core.domain.ReviewerStatus;
import org.sadtech.bot.vcs.core.domain.entity.Person;
import org.sadtech.bot.vcs.core.domain.entity.PullRequest;
import org.sadtech.bot.vcs.core.domain.notify.GoodMorningNotify;
import org.sadtech.bot.vcs.core.domain.notify.SimpleTextNotify;
import org.sadtech.bot.vcs.core.service.NotifyService;
import org.sadtech.bot.vcs.core.service.PersonService;
import org.sadtech.bot.vcs.core.service.PullRequestsService;
import org.sadtech.bot.vcs.core.utils.Smile;
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
public class NotificationScheduler {

    private static final Set<String> tksLoginNotify = new HashSet<>(Arrays.asList(
            "mstruchkov", "emukhin", "imescheryakov", "kkeglev"
    ));

    private final PersonService personService;
    private final PullRequestsService pullRequestsService;

    private final NotifyService notifyService;

    private final AppProperty appProperty;

    // Утреннее сообщение
    @Scheduled(cron = "0 15 8 * * MON-FRI")
    public void goodMorning() {
        List<Person> allRegister = personService.getAllRegister();
        for (Person user : allRegister) {
            List<PullRequest> pullRequestsReviews = pullRequestsService.getAllByReviewerAndStatuses(
                    user.getLogin(),
                    ReviewerStatus.NEEDS_WORK,
                    Collections.singleton(PullRequestStatus.OPEN)
            );
            List<PullRequest> pullRequestsNeedWork = pullRequestsService.getAllByAuthorAndReviewerStatus(user.getLogin(), ReviewerStatus.UNAPPROVED);
            notifyService.send(
                    GoodMorningNotify.builder()
                            .personName(user.getFullName())
                            .pullRequestsNeedWork(pullRequestsNeedWork)
                            .pullRequestsReviews(pullRequestsReviews)
                            .recipients(Collections.singleton(user.getLogin()))
                            .version(appProperty.getVersion())
                            .build()
            );
        }
    }

    @Scheduled(cron = "0 44 10 * * MON-FRI")
    public void tks() {
        List<Person> usersTks = personService.getAllRegister().stream()
                .filter(user -> tksLoginNotify.contains(user.getLogin()))
                .collect(Collectors.toList());
        notifyService.send(
                SimpleTextNotify
                        .builder()
                        .recipients(
                                usersTks.stream()
                                        .map(Person::getLogin)
                                        .collect(Collectors.toSet())
                        )
                        .message("☎️ Внимание созвон" + Smile.HR + "https://meet.google.com/czs-vigu-mte")
                        .build()
        );

    }

    @Scheduled(cron = "0 0 18 * * FRI")
    public void goodWeekEnd() {
        List<Person> allRegister = personService.getAllRegister();
        notifyService.send(
                SimpleTextNotify.builder()
                        .entityType(EntityType.PERSON)
                        .message("Ну вот и все! Веселых выходных " + Smile.MIG + Smile.BR +
                                "До понедельника" + Smile.BUY + Smile.TWO_BR)
                        .recipients(
                                allRegister.stream()
                                        .map(Person::getLogin)
                                        .collect(Collectors.toSet())
                        )
                        .build()
        );
    }
}


