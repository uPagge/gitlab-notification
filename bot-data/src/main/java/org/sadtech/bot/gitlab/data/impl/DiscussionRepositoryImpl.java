package org.sadtech.bot.gitlab.data.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Discussion;
import org.sadtech.bot.gitlab.context.repository.DiscussionRepository;
import org.sadtech.bot.gitlab.data.jpa.DiscussionJpaRepository;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * // TODO: 11.02.2021 Добавить описание.
 *
 * @author upagge 11.02.2021
 */
@Repository
public class DiscussionRepositoryImpl extends AbstractSimpleManagerRepository<Discussion, String> implements DiscussionRepository {

    private final DiscussionJpaRepository jpaRepository;

    public DiscussionRepositoryImpl(DiscussionJpaRepository jpaRepository) {
        super(jpaRepository);
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Discussion> findAllByMergeRequestId(@NonNull Long mergeRequestId) {
        return jpaRepository.findAllByMergeRequestId(mergeRequestId);
    }

}
