package org.sadtech.bot.bitbucketbot.scheduler.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.bitbucketbot.service.parser.PullRequestParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PullRequestParserScheduler {

    private final PullRequestParser pullRequestParser;

    @Scheduled(cron = "")
    public void parsingOldPullRequest() {
        pullRequestParser.parsingOldPullRequest();
    }

    @Scheduled(cron = "")
    public void parsingNewPullRequest() {
        pullRequestParser.parsingNewPullRequest();
    }

}
