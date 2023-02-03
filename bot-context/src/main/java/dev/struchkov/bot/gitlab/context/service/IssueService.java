package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.*;
import dev.struchkov.bot.gitlab.context.domain.entity.Issue;
import lombok.NonNull;

import java.util.List;
import java.util.Set;

/**
 * @author Dmitry Sheyko [24.01.2023]
 */
public interface IssueService {

    Issue create(@NonNull Issue issue);

    Issue update(@NonNull Issue issue);

    List<Issue> updateAll(@NonNull List<Issue> issues);

    ExistContainer<Issue, Long> existsById(@NonNull Set<Long> issueIds);

    List<Issue> createAll(List<Issue> issues);

    Set<IdAndStatusIssue> getAllId(Set<IssueState> statuses);

    void cleanOld();

}