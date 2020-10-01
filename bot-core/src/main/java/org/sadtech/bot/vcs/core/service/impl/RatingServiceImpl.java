package org.sadtech.bot.vcs.core.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.core.domain.PointType;
import org.sadtech.bot.vcs.core.domain.entity.RatingHistory;
import org.sadtech.bot.vcs.core.repository.RatingHistoryRepository;
import org.sadtech.bot.vcs.core.service.RatingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * // TODO: 01.10.2020 Добавить описание.
 *
 * @author upagge 01.10.2020
 */
@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingHistoryRepository ratingHistoryRepository;

    @Override
    public void addRating(@NonNull String login, @NonNull PointType type, @NonNull Integer points) {
        final RatingHistory ratingHistory = new RatingHistory();
        ratingHistory.setLogin(login);
        ratingHistory.setPoints(points);
        ratingHistory.setType(type);
        ratingHistory.setDateAdd(LocalDateTime.now());
        ratingHistoryRepository.save(ratingHistory);
    }

}
