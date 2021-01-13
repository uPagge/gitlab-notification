package org.sadtech.bot.vcs.bitbucketbot.data.jpa;


import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.RatingList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * // TODO: 01.10.2020 Добавить описание.
 *
 * @author upagge 01.10.2020
 */
public interface RatingListJpaRepository extends JpaRepository<RatingList, String> {

    List<RatingList> findTop3ByPointsGreaterThanOrderByNumberAsc(Integer points);

    List<RatingList> findTop3ByPointsGreaterThanOrderByNumberDesc(Integer points);

    Long countByNumberGreaterThan(Integer points);

}
