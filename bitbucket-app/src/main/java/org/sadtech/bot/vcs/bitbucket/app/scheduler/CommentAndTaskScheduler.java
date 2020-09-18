package org.sadtech.bot.vcs.bitbucket.app.scheduler;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.bitbucket.app.service.CommentAndTaskParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentAndTaskScheduler {

    private final CommentAndTaskParser commentAndTaskParser;

    @Scheduled(cron = "0 */1 * * * *")
    public void scanNewCommentAndTask() {
        commentAndTaskParser.scanNewCommentAndTask();
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void scanOldComment() {
        commentAndTaskParser.scanOldComment();
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void scanOldTask() {
        commentAndTaskParser.scanOldTask();
    }

}
