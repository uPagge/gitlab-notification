package org.sadtech.bot.gitlab.data.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequestMini;
import org.sadtech.bot.gitlab.context.repository.MergeRequestRepository;
import org.sadtech.bot.gitlab.data.jpa.MergeRequestJpaRepository;
import org.sadtech.bot.gitlab.data.jpa.MergeRequestMiniJpaRepository;
import org.sadtech.haiti.database.repository.manager.FilterManagerRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public class MergeRequestRepositoryImpl extends FilterManagerRepository<MergeRequest, Long> implements MergeRequestRepository {

    private final MergeRequestJpaRepository repositoryJpa;
    private final MergeRequestMiniJpaRepository mergeRequestMiniJpaRepository;

    public MergeRequestRepositoryImpl(MergeRequestJpaRepository jpaRepository, MergeRequestMiniJpaRepository mergeRequestMiniJpaRepository) {
        super(jpaRepository);
        repositoryJpa = jpaRepository;
        this.mergeRequestMiniJpaRepository = mergeRequestMiniJpaRepository;
    }

    @Override
    public Set<IdAndStatusPr> findAllIdByStateIn(Set<MergeRequestState> statuses) {
        return repositoryJpa.findAllIdByStateIn(statuses);
    }

    @Override
    public Optional<MergeRequestMini> findMiniInfoById(@NonNull Long id) {
        return mergeRequestMiniJpaRepository.findById(id);
    }

}
