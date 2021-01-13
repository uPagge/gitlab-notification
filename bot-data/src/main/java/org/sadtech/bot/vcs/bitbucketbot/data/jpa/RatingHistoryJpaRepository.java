package org.sadtech.bot.vcs.bitbucketbot.data.jpa;


import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.RatingHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * // TODO: 01.10.2020 Добавить описание.
 *
 * @author upagge 01.10.2020
 */
public interface RatingHistoryJpaRepository extends JpaRepository<RatingHistory, Long> {

    List<RatingHistory> findAllByDateAddBetween(LocalDateTime from, LocalDateTime to);

}
