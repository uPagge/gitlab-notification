//package com.tsc.bitbucketbot.scheduler;
//
//import com.tsc.bitbucketbot.domain.entity.User;
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
//
//    @Scheduled(cron = "0 9 * * MON-FRI")
//    public void goodMorning() {
//        List<User> users = userService.getAllRegister();
//    }
//
//}
