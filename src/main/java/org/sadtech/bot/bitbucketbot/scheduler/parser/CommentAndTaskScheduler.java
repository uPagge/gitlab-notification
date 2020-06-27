package org.sadtech.bot.bitbucketbot.scheduler.parser;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.service.parser.CommentAndTaskParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentAndTaskScheduler {

    private final CommentAndTaskParser commentAndTaskParser;

    @Scheduled(cron = "")
    public void scanNewCommentAndTask() {
        commentAndTaskParser.scanNewCommentAndTask();
    }

    @Scheduled(cron = "")
    public void scanOldComment() {
        commentAndTaskParser.scanOldComment();
    }

}
