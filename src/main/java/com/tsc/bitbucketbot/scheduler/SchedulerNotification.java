//package com.tsc.bitbucketbot.scheduler;
//
//import com.tsc.bitbucketbot.domain.entity.PullRequest;
//import com.tsc.bitbucketbot.domain.entity.User;
//import com.tsc.bitbucketbot.service.PullRequestsService;
//import com.tsc.bitbucketbot.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class SchedulerNotification {
//
//    private final UserService userService;
//    private final PullRequestsService pullRequestsService;
//
////    @Scheduled(cron = "0 9 * * MON-FRI")
//    @Scheduled(fixedRate = 50000)
//    public void goodMorning() {
//        List<User> users = userService.getAllRegister();
//        List<PullRequest> mstruchkov = pullRequestsService.getAllByReviewer("mstruchkov");
//        System.out.println();
//    }
//
//}
