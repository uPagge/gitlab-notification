package org.sadtech.bot.vcs.bitbucket.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.vcs.core.service.parser.PullRequestParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Позволяет задать время парсинга для ПРов.
 *
 * @author upagge 06.09.2020
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PullRequestParserScheduler {

    private final PullRequestParser pullRequestParser;

    @Scheduled(cron = "0 */1 * * * *")
    public void parsingOldPullRequest() {
        pullRequestParser.parsingOldPullRequest();
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void parsingNewPullRequest() {
        pullRequestParser.parsingNewPullRequest();
    }

}
