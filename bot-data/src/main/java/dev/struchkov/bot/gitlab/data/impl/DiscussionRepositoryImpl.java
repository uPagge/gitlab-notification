package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import dev.struchkov.bot.gitlab.context.repository.DiscussionRepository;
import dev.struchkov.bot.gitlab.data.jpa.DiscussionJpaRepository;
import dev.struchkov.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
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
