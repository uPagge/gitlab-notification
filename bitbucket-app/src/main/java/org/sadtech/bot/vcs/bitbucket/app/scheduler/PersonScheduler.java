package org.sadtech.bot.vcs.bitbucket.app.scheduler;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.bitbucket.app.service.parser.PersonBitbucketParser;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersonScheduler {

    private final PersonBitbucketParser personParser;

    @Scheduled(cron = "${bitbucketbot.scheduler.person:0 0 0 */1 * *}")
    public void scanPersons() {
        personParser.scanNewPerson();
    }

}
