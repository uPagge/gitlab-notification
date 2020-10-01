package org.sadtech.bot.vcs.core.repository.impl;

import org.sadtech.basic.database.repository.manager.AbstractSimpleManagerRepository;
import org.sadtech.bot.vcs.core.domain.entity.RatingList;
import org.sadtech.bot.vcs.core.repository.RatingListRepository;
import org.sadtech.bot.vcs.core.repository.jpa.RatingListJpaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * // TODO: 01.10.2020 Добавить описание.
 *
 * @author upagge 01.10.2020
 */
@Repository
public class RatingListRepositoryImpl extends AbstractSimpleManagerRepository<RatingList, String> implements RatingListRepository {

    private final RatingListJpaRepository jpaRepository;

    public RatingListRepositoryImpl(RatingListJpaRepository jpaRepository) {
        super(jpaRepository);
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<RatingList> getByLogin(String login) {
        return jpaRepository.findById(login);
    }

    @Override
    public List<RatingList> findFirstThree() {
        return jpaRepository.findAll(
                PageRequest.of(0, 3, Sort.by(Sort.Direction.ASC, "number"))
        ).getContent();
    }

    @Override
    public List<RatingList> findLastThree() {
        return jpaRepository.findAll(
                PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "number"))
        ).getContent();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

}
