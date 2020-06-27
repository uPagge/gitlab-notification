package org.sadtech.bot.bitbucketbot.scheduler.parser;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.service.parser.PersonParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonScheduler {

    private final PersonParser personParser;

    @Scheduled(cron = "")
    public void scanPersons() {
        personParser.scanNewPerson();
    }


}
