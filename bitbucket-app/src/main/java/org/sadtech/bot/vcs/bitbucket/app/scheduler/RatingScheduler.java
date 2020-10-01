package org.sadtech.bot.vcs.bitbucket.app.scheduler;

/**
 * // TODO: 01.10.2020 Добавить описание.
 *
 * @author upagge 01.10.2020
 */

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.core.service.RatingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RatingScheduler {

    private final RatingService ratingService;

    @Scheduled(cron = "0 */1 * * * *")
    private void ratingRecalculation() {
        ratingService.ratingRecalculation();
    }

}
