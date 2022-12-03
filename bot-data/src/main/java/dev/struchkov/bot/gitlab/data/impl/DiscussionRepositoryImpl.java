package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import dev.struchkov.bot.gitlab.context.repository.DiscussionRepository;
import dev.struchkov.bot.gitlab.data.jpa.DiscussionJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author upagge 11.02.2021
 */
@Repository
@RequiredArgsConstructor
public class DiscussionRepositoryImpl implements DiscussionRepository {

    private final DiscussionJpaRepository jpaRepository;

    @Override
    public List<Discussion> findAllByMergeRequestId(@NonNull Long mergeRequestId) {
        return jpaRepository.findAllByMergeRequestId(mergeRequestId);
    }

    @Override
    public Discussion save(Discussion discussion) {
        return jpaRepository.save(discussion);
    }

    @Override
    public Optional<Discussion> findById(String discussionId) {
        return jpaRepository.findById(discussionId);
    }

    @Override
    public void deleteById(String discussionId) {
        jpaRepository.deleteById(discussionId);
    }

    @Override
    public Page<Discussion> findAll(Pageable pagination) {
        return jpaRepository.findAll(pagination);
    }

    @Override
    public List<Discussion> findAllById(Set<String> discussionIds) {
        return jpaRepository.findAllById(discussionIds);
    }

}
