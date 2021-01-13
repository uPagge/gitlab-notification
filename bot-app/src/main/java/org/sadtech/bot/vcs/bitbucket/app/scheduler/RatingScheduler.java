package org.sadtech.bot.vcs.bitbucket.app.scheduler;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.EntityType;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Person;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.notify.SimpleTextNotify;
import org.sadtech.bot.vsc.bitbucketbot.context.service.NotifyService;
import org.sadtech.bot.vsc.bitbucketbot.context.service.PersonService;
import org.sadtech.bot.vsc.bitbucketbot.context.service.RatingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * // TODO: 01.10.2020 Добавить описание.
 *
 * @author upagge 01.10.2020
 */
@Component
@RequiredArgsConstructor
public class RatingScheduler {

    private final RatingService ratingService;
    private final PersonService personService;
    private final NotifyService notifyService;

    @Scheduled(cron = "0 */50 * * * *")
    private void ratingRecalculation() {
        ratingService.ratingRecalculation();
    }

    @Scheduled(cron = "0 20 8 * * MON-FRI")
    private void goodMorningRating() {
        List<Person> allRegister = personService.getAllRegister();
        for (Person person : allRegister) {
            final String message = ratingService.getRatingTop(person.getLogin());
            notifyService.send(
                    SimpleTextNotify.builder()
                            .entityType(EntityType.PERSON)
                            .message(message)
                            .recipients(Collections.singleton(person.getLogin()))
                            .build()
            );
        }
    }

}
