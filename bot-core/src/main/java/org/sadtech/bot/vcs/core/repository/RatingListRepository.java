package org.sadtech.bot.vcs.core.repository;

import org.sadtech.basic.context.repository.SimpleManagerRepository;
import org.sadtech.bot.vcs.core.domain.entity.RatingList;

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
