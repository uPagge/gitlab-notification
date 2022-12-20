package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.repository.MergeRequestRepository;
import dev.struchkov.bot.gitlab.data.jpa.MergeRequestJpaRepository;
import dev.struchkov.haiti.filter.Filter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class MergeRequestRepositoryImpl implements MergeRequestRepository {

    private final MergeRequestJpaRepository jpaRepository;

    @Override
    public Set<IdAndStatusPr> findAllIdByStateIn(@NonNull Set<MergeRequestState> statuses) {
        return jpaRepository.findAllIdByStateIn(statuses);
    }

    @Override
    public MergeRequest save(MergeRequest mergeRequest) {
        return jpaRepository.save(mergeRequest);
    }

    @Override
    public Optional<MergeRequest> findById(Long mergeRequestId) {
        return jpaRepository.findById(mergeRequestId);
    }

    @Override
    public List<MergeRequest> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<MergeRequest> findAllById(Set<Long> mergeRequestIds) {
        return jpaRepository.findAllById(mergeRequestIds);
    }

    @Override
    public void deleteByIds(Set<Long> mergeRequestIds) {
        jpaRepository.deleteAllByIdIn(mergeRequestIds);
    }

    @Override
    public Page<MergeRequest> filter(Filter filter, Pageable pageable) {
        return jpaRepository.findAll(filter.<Specification<MergeRequest>>build(), pageable);
    }

    @Override
    public List<MergeRequest> findAllByReviewerId(Long personId) {
        return jpaRepository.findAllByReviewersIn(personId);
    }

}
