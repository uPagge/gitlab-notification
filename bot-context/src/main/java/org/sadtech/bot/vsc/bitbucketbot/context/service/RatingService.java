package org.sadtech.bot.vsc.bitbucketbot.context.service;

import lombok.NonNull;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.PointType;

/**
 * // TODO: 01.10.2020 Добавить описание.
 *
 * @author upagge 01.10.2020
 */
public interface RatingService {

    void addRating(@NonNull String login, @NonNull PointType type, @NonNull Integer points);

    void ratingRecalculation();

    String getRatingTop(@NonNull String login);

}
