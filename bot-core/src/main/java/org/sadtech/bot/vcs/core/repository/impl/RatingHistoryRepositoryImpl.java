package org.sadtech.bot.vcs.core.repository.impl;

import lombok.NonNull;
import org.sadtech.basic.database.repository.manager.AbstractSimpleManagerRepository;
import org.sadtech.bot.vcs.core.domain.entity.RatingHistory;
import org.sadtech.bot.vcs.core.repository.RatingHistoryRepository;
import org.sadtech.bot.vcs.core.repository.jpa.RatingHistoryJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * // TODO: 01.10.2020 Добавить описание.
 *
 * @author upagge 01.10.2020
 */
@Repository
public class RatingHistoryRepositoryImpl extends AbstractSimpleManagerRepository<RatingHistory, Long> implements RatingHistoryRepository {

    private final RatingHistoryJpaRepository jpaRepository;

    public RatingHistoryRepositoryImpl(RatingHistoryJpaRepository jpaRepository) {
        super(jpaRepository);
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<RatingHistory> findAllByDateAddBetween(@NonNull LocalDateTime from, @NonNull LocalDateTime to) {
        return jpaRepository.findAllByDateAddBetween(from, to);
    }

}