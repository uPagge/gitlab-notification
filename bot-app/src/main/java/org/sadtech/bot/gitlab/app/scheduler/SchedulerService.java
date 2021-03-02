package org.sadtech.bot.gitlab.app.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.gitlab.context.service.CleanService;
import org.sadtech.bot.gitlab.core.service.parser.DiscussionParser;
import org.sadtech.bot.gitlab.core.service.parser.MergeRequestParser;
import org.sadtech.bot.gitlab.core.service.parser.PipelineParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * // TODO: 14.01.2021 Добавить описание.
 *
 * @author upagge 14.01.2021
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final PipelineParser pipelineParser;
    private final MergeRequestParser mergeRequestParser;
    private final CleanService cleanService;
    private final DiscussionParser discussionParser;

    @Scheduled(cron = "0 */1 * * * *")
    public void newMergeRequest() {
        mergeRequestParser.parsingOldMergeRequest();
        mergeRequestParser.parsingNewMergeRequest();
        pipelineParser.scanOldPipeline();
        pipelineParser.scanNewPipeline();
        discussionParser.scanOldDiscussions();
        discussionParser.scanNewDiscussion();
        cleanService.cleanOldPipelines();
        cleanService.cleanMergedPullRequests();
    }

}
