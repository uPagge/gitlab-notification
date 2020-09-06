package org.sadtech.bot.bitbucketbot.scheduler.parser;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.service.impl.parser.PersonBitbucketParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonScheduler {

    private final PersonBitbucketParser personParser;

    //    @Scheduled(cron = "0 0 0 */1 * *")
    @Scheduled(cron = "0 */1 * * * *")
    public void scanPersons() {
        personParser.scanNewPerson();
    }

}
