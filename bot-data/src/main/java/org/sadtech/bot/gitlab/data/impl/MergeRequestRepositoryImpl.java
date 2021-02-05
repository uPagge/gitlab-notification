package org.sadtech.bot.gitlab.data.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.repository.MergeRequestRepository;
import org.sadtech.bot.gitlab.data.jpa.MergeRequestJpaRepository;
import org.sadtech.haiti.database.repository.manager.FilterManagerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class MergeRequestRepositoryImpl extends FilterManagerRepository<MergeRequest, Long> implements MergeRequestRepository {

    private final MergeRequestJpaRepository repositoryJpa;

    public MergeRequestRepositoryImpl(MergeRequestJpaRepository jpaRepository) {
        super(jpaRepository);
        repositoryJpa = jpaRepository;
    }

    @Override
    public Set<IdAndStatusPr> findAllIdByStateIn(Set<MergeRequestState> statuses) {
        return repositoryJpa.findAllIdByStateIn(statuses);
    }

    @Override
    public List<MergeRequest> findAllByAssignee(@NonNull Long userId) {
        return repositoryJpa.findAllByAssigneeId(userId);
    }

}
