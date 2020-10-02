package org.sadtech.bot.vcs.core.repository;

import lombok.NonNull;
import org.sadtech.basic.context.repository.SimpleManagerRepository;
import org.sadtech.bot.vcs.core.domain.entity.RatingHistory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * // TODO: 01.10.2020 Добавить описание.
 *
 * @author upagge 01.10.2020
 */
public interface RatingHistoryRepository extends SimpleManagerRepository<RatingHistory, Long> {

    List<RatingHistory> findAllByDateAddBetween(@NonNull LocalDateTime from, @NonNull LocalDateTime to);

}