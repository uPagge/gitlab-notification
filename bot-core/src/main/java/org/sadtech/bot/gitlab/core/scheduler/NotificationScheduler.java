package org.sadtech.bot.gitlab.core.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationScheduler {
//
//    private final PullRequestsService pullRequestsService;
//    private final NotifyService notifyService;
//    private final AppProperty appProperty;
//
//    // Утреннее сообщение
//    @Scheduled(cron = "0 15 8 * * MON-FRI")
//    public void goodMorning() {
//        List<Person> allRegister = personService.getAllRegister();
//        for (Person user : allRegister) {
//            List<PullRequest> pullRequestsReviews = pullRequestsService.getAllByReviewerAndStatuses(
//                    user.getLogin(),
//                    ReviewerStatus.NEEDS_WORK,
//                    Collections.singleton(PullRequestStatus.OPEN)
//            );
//            List<PullRequest> pullRequestsNeedWork = pullRequestsService.getAllByAuthorAndReviewerStatus(user.getLogin(), ReviewerStatus.UNAPPROVED);
//            notifyService.send(
//                    GoodMorningNotify.builder()
//                            .personName(user.getFullName())
//                            .pullRequestsNeedWork(pullRequestsNeedWork)
//                            .pullRequestsReviews(pullRequestsReviews)
//                            .recipients(Collections.singleton(user.getLogin()))
//                            .version(appProperty.getVersion())
//                            .build()
//            );
//        }
//    }
//
//    @Scheduled(cron = "0 44 10 * * MON-FRI")
//    public void tks() {
//        notifyService.send(
//                SimpleTextNotify
//                        .builder()
//                        .recipients(
//                                usersTks.stream()
//                                        .map(Person::getLogin)
//                                        .collect(Collectors.toSet())
//                        )
//                        .message("☎️ Внимание созвон" + Smile.HR + "https://meet.google.com/avj-cdyy-enu")
//                        .build()
//        );
//    }
//
//    @Scheduled(cron = "0 0 18 * * FRI")
//    public void goodWeekEnd() {
//        List<Person> allRegister = personService.getAllRegister();
//        notifyService.send(
//                SimpleTextNotify.builder()
//                        .entityType(EntityType.PERSON)
//                        .message("Ну вот и все! Веселых выходных " + Smile.MIG + Smile.BR +
//                                "До понедельника" + Smile.BUY + Smile.TWO_BR)
//                        .recipients(
//                                allRegister.stream()
//                                        .map(Person::getLogin)
//                                        .collect(Collectors.toSet())
//                        )
//                        .build()
//        );
//    }
}


