package org.sadtech.bot.gitlab.app.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.gitlab.core.service.parser.MergeRequestParser;
import org.sadtech.bot.gitlab.core.service.parser.NoteParser;
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

    private final MergeRequestParser mergeRequestParser;
    private final NoteParser noteParser;

    @Scheduled(cron = "*/30 * * * * *")
    public void newMergeRequest() {
        mergeRequestParser.parsingNewMergeRequest();
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void oldMergeRequest() {
        mergeRequestParser.parsingOldMergeRequest();
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void newNoteParser() {
        noteParser.scanNewCommentAndTask();
    }

    @Scheduled(cron = "*/30 * * * * *")
    public void oldTaskParser() {
        noteParser.scanOldTask();
    }

}
