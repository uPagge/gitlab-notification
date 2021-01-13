package org.sadtech.bot.vcs.bitbucketbot.data.impl;

import lombok.NonNull;
import org.sadtech.bot.vcs.bitbucketbot.data.jpa.RatingHistoryJpaRepository;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.RatingHistory;
import org.sadtech.bot.vsc.bitbucketbot.context.repository.RatingHistoryRepository;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;
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
