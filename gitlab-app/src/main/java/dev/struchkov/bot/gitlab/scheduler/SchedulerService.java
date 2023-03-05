package dev.struchkov.bot.gitlab.scheduler;

import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.service.DiscussionService;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.bot.gitlab.context.service.PipelineService;
import dev.struchkov.bot.gitlab.core.service.parser.DiscussionParser;
import dev.struchkov.bot.gitlab.core.service.parser.MergeRequestParser;
import dev.struchkov.bot.gitlab.core.service.parser.PipelineParser;
import dev.struchkov.bot.gitlab.core.service.parser.ProjectParser;
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

    private final MergeRequestParser mergeRequestParser;
    private final ProjectParser projectParser;
    private final PipelineParser pipelineParser;
    private final DiscussionParser discussionParser;

    private final PipelineService pipelineService;
    private final MergeRequestsService mergeRequestsService;
    private final DiscussionService discussionService;

    @Scheduled(cron = "0 0 */1 * * *")
    public void newProjects() {
        log.info("Запуск процесса получение новых репозиториев c GitLab");
        if (!settingService.isFirstStart()) {
            if (settingService.isOwnerProjectScan()) {
                projectParser.parseAllProjectOwner();
            }
            if (settingService.isPrivateProjectScan()) {
                projectParser.parseAllPrivateProject();
            }
        }
        log.info("Конец процесса получение новых репозиториев c GitLab");
    }

    @Scheduled(cron = "0 */15 * * * *")
    public void newMergeRequests() {
        log.info("Запуск процесса получение новых MR c GitLab");
        if (!settingService.isFirstStart()) {
            mergeRequestParser.parsingNewMergeRequest();
        }
        log.info("Конец процесса получение новых MR c GitLab");
    }

    @Scheduled(cron = "0 */1 * * * *")
    public void newMergeRequest() {
        log.info("Запуск процесса обновления данных c GitLab");
        if (!settingService.isFirstStart()) {
            mergeRequestParser.parsingOldMergeRequest();
            pipelineParser.scanOldPipeline();
            pipelineParser.scanNewPipeline();
            discussionParser.scanOldDiscussions();
            discussionParser.scanNewDiscussion();
            mergeRequestsService.cleanOld();
            discussionService.cleanOld();
            pipelineService.cleanOld();
        } else {
            log.warn("Процесс обновления данных не был выполнен, так как пользователь не выполнил первичную настройку.");
        }
        log.info("Конец процесса обновления данных c GitLab");
    }

}
