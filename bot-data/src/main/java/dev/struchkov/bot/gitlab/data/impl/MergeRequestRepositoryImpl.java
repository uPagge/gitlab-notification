package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.repository.MergeRequestRepository;
import dev.struchkov.bot.gitlab.data.jpa.MergeRequestJpaRepository;
import dev.struchkov.haiti.database.repository.manager.FilterManagerRepository;
import lombok.NonNull;
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
    public Set<IdAndStatusPr> findAllIdByStateIn(@NonNull Set<MergeRequestState> statuses) {
        return repositoryJpa.findAllIdByStateIn(statuses);
    }

    @Override
    public List<MergeRequest> findAllByAssignee(@NonNull Long userId) {
        return repositoryJpa.findAllByAssigneeId(userId);
    }

}
