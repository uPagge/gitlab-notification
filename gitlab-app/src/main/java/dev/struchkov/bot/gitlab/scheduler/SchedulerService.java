package dev.struchkov.bot.gitlab.scheduler;

import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.bot.gitlab.context.service.PipelineService;
import dev.struchkov.bot.gitlab.core.service.parser.DiscussionParser;
import dev.struchkov.bot.gitlab.core.service.parser.MergeRequestParser;
import dev.struchkov.bot.gitlab.core.service.parser.PipelineParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author upagge 14.01.2021
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final AppSettingService settingService;

    private final PipelineParser pipelineParser;
    private final MergeRequestParser mergeRequestParser;
    private final DiscussionParser discussionParser;

    private final PipelineService pipelineService;
    private final MergeRequestsService mergeRequestsService;

    @Scheduled(cron = "0 */1 * * * *")
    public void newMergeRequest() {
        log.debug("Запуск процесса обновления данных");
        if (!settingService.isFirstStart()) {
            mergeRequestParser.parsingOldMergeRequest();
            mergeRequestParser.parsingNewMergeRequest();
            pipelineParser.scanOldPipeline();
            pipelineParser.scanNewPipeline();
            discussionParser.scanOldDiscussions();
            discussionParser.scanNewDiscussion();
            mergeRequestsService.cleanOld();
            pipelineService.cleanOld();
        } else {
            log.warn("Процесс обновления данных не был выполнен, так как пользователь не выполнил первичную настройку.");
        }
        log.debug("Конец процесса обновления данных");
    }

}
