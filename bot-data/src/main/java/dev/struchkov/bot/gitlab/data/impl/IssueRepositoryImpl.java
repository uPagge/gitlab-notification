package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.IdAndStatusIssue;
import dev.struchkov.bot.gitlab.context.domain.IssueState;
import dev.struchkov.bot.gitlab.context.domain.entity.Issue;
import dev.struchkov.bot.gitlab.context.repository.IssueRepository;
import dev.struchkov.bot.gitlab.data.jpa.IssueJpaRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Dmitry Sheyko [24.01.2023]
 */
@Repository
@RequiredArgsConstructor
public class IssueRepositoryImpl implements IssueRepository {

    private final IssueJpaRepository jpaRepository;

    @Override
    @Transactional(readOnly = true)
    public Set<IdAndStatusIssue> findAllIdByStateIn(@NonNull Set<IssueState> statuses) {
        return jpaRepository.findAllIdByStateIn(statuses);
    }

    @Override
    @Transactional
    public Issue save(Issue issue) {
        return jpaRepository.save(issue);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Issue> findById(Long issueId) {
        return jpaRepository.findById(issueId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Issue> findAllById(Set<Long> issueIds) {
        return jpaRepository.findAllById(issueIds);
    }

    @Override
    @Transactional
    public void deleteByStates(Set<IssueState> states) {
        jpaRepository.deleteAllByStateIn(states);
    }

}