package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.IdAndStatusIssue;
import dev.struchkov.bot.gitlab.context.domain.IssueState;
import dev.struchkov.bot.gitlab.context.domain.entity.Issue;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Dmitry Sheyko [24.01.2023]
 */
public interface IssueRepository {

    Set<IdAndStatusIssue> findAllIdByStateIn(@NonNull Set<IssueState> states);

    Issue save(Issue issue);

    Optional<Issue> findById(Long issueId);

    List<Issue> findAllById(Set<Long> mergeRequestIds);

    void deleteByStates(Set<IssueState> states);

}