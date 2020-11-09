package org.sadtech.bot.vsc.bitbucketbot.context.repository;

import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.RatingList;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;

import java.util.List;
import java.util.Optional;

/**
 * // TODO: 01.10.2020 Добавить описание.
 *
 * @author upagge 01.10.2020
 */
public interface RatingListRepository extends SimpleManagerRepository<RatingList, String> {

    Optional<RatingList> getByLogin(String login);

    List<RatingList> findFirstThree();

    List<RatingList> findLastThree();

    long count();

}
