package org.sadtech.bot.gitlab.app.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.gitlab.app.service.parser.MergeRequestParser;
import org.sadtech.bot.gitlab.app.service.parser.ProjectParser;
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

    private final ProjectParser projectParser;
    private final MergeRequestParser mergeRequestParser;

    @Scheduled(cron = "*/30 * * * * *")
    public void newProjectParse() {
        projectParser.parseNewProject();
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void newMergeRequest() {
        mergeRequestParser.parsingNewMergeRequest();
    }

}
