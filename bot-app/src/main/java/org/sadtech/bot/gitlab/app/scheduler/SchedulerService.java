package org.sadtech.bot.gitlab.app.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.gitlab.context.service.CleanService;
import org.sadtech.bot.gitlab.core.service.parser.DiscussionParser;
import org.sadtech.bot.gitlab.core.service.parser.MergeRequestParser;
import org.sadtech.bot.gitlab.core.service.parser.NoteParser;
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
    private final NoteParser noteParser;
    private final CleanService cleanService;
    private final DiscussionParser discussionParser;

    @Scheduled(cron = "*/30 * * * * *")
    public void newMergeRequest() {
        mergeRequestParser.parsingNewMergeRequest();
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void oldMergeRequest() {
        mergeRequestParser.parsingOldMergeRequest();
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void newDiscussion() {
        discussionParser.scanNewDiscussion();
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void newPipeline() {
        pipelineParser.scanNewPipeline();
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void oldPipeline() {
        pipelineParser.scanOldPipeline();
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void clean() {
        cleanService.cleanOldPipelines();
        cleanService.cleanMergedPullRequests();
    }

}
