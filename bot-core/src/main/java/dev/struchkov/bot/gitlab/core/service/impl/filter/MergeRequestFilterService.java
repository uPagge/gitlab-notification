package dev.struchkov.bot.gitlab.core.service.impl.filter;

import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest_;
import dev.struchkov.bot.gitlab.context.domain.filter.MergeRequestFilter;
import dev.struchkov.bot.gitlab.context.repository.MergeRequestRepository;
import dev.struchkov.haiti.core.service.AbstractFilterService;
import dev.struchkov.haiti.filter.Filter;
import dev.struchkov.haiti.filter.FilterQuery;
import dev.struchkov.haiti.filter.criteria.CriteriaFilter;
import dev.struchkov.haiti.filter.criteria.CriteriaQuery;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class MergeRequestFilterService extends AbstractFilterService<MergeRequest, MergeRequestFilter> {

    public MergeRequestFilterService(MergeRequestRepository filterOperation) {
        super(filterOperation);
    }

    @Override
    protected Filter createFilter(@NonNull MergeRequestFilter filter) {
        return CriteriaFilter.<MergeRequest>create()
                .and(convertFilter(filter))
                .or(convertFilterOr(filter));
    }

    private FilterQuery convertFilterOr(MergeRequestFilter filter) {
        return CriteriaQuery.<MergeRequest>create()
                .matchPhrase(MergeRequest_.STATE, filter.getStates());
    }

    private FilterQuery convertFilter(@NonNull MergeRequestFilter filter) {
        return CriteriaQuery.<MergeRequest>create()
                .matchPhrase(MergeRequest_.ASSIGNEE, filter.getAssignee());
    }

}
