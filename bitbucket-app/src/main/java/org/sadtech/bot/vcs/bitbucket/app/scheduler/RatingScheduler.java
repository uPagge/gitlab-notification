package org.sadtech.bot.vcs.bitbucket.app.scheduler;

/**
 * // TODO: 01.10.2020 Добавить описание.
 *
 * @author upagge 01.10.2020
 */

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.core.domain.EntityType;
import org.sadtech.bot.vcs.core.domain.entity.Person;
import org.sadtech.bot.vcs.core.domain.notify.SimpleTextNotify;
import org.sadtech.bot.vcs.core.service.NotifyService;
import org.sadtech.bot.vcs.core.service.PersonService;
import org.sadtech.bot.vcs.core.service.RatingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

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
