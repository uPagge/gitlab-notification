package dev.struchkov.bot.gitlab.core.service.impl.filter;

import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequestFields;
import dev.struchkov.bot.gitlab.context.domain.filter.MergeRequestFilter;
import dev.struchkov.bot.gitlab.context.repository.MergeRequestRepository;
import dev.struchkov.haiti.filter.Filter;
import dev.struchkov.haiti.filter.FilterQuery;
import dev.struchkov.haiti.filter.criteria.CriteriaFilter;
import dev.struchkov.haiti.filter.criteria.CriteriaQuery;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MergeRequestFilterService {

    private final MergeRequestRepository repository;

    public Page<MergeRequest> getAll(MergeRequestFilter filter, Pageable pagination) {
        return repository.filter(createFilter(filter), pagination);
    }

    private Filter createFilter(@NonNull MergeRequestFilter filter) {
        return CriteriaFilter.<MergeRequest>create()
                .and(convertFilter(filter))
                .or(convertFilterOr(filter));
    }

    private FilterQuery convertFilterOr(MergeRequestFilter filter) {
        return CriteriaQuery.<MergeRequest>create()
                .matchPhrase(MergeRequestFields.state, filter.getStates());
    }

    private FilterQuery convertFilter(@NonNull MergeRequestFilter filter) {
        return CriteriaQuery.<MergeRequest>create()
                .matchPhrase(MergeRequestFields.assignee, filter.getAssignee());
    }

}
