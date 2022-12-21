package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequestForDiscussion;
import dev.struchkov.bot.gitlab.context.repository.MergeRequestRepository;
import dev.struchkov.bot.gitlab.data.jpa.MergeRequestForDiscussionJpaRepository;
import dev.struchkov.bot.gitlab.data.jpa.MergeRequestJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class MergeRequestRepositoryImpl implements MergeRequestRepository {

    private final MergeRequestJpaRepository jpaRepository;
    private final MergeRequestForDiscussionJpaRepository forDiscussionJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public Set<IdAndStatusPr> findAllIdByStateIn(@NonNull Set<MergeRequestState> statuses) {
        return jpaRepository.findAllIdByStateIn(statuses);
    }

    @Override
    @Transactional
    public MergeRequest save(MergeRequest mergeRequest) {
        return jpaRepository.save(mergeRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MergeRequest> findById(Long mergeRequestId) {
        return jpaRepository.findById(mergeRequestId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MergeRequestForDiscussion> findAllForDiscussion() {
        return forDiscussionJpaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MergeRequest> findAllById(Set<Long> mergeRequestIds) {
        return jpaRepository.findAllById(mergeRequestIds);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MergeRequest> findAllByReviewerId(Long personId) {
        return jpaRepository.findAllByReviewersIn(personId);
    }

    @Override
    @Transactional
    public void deleteByStates(Set<MergeRequestState> states) {
        jpaRepository.deleteAllByStateIn(states);
    }

}
