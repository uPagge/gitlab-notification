package org.sadtech.bot.gitlab.core.service.impl.filter;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest_;
import org.sadtech.bot.gitlab.context.domain.filter.MergeRequestFilter;
import org.sadtech.bot.gitlab.context.repository.MergeRequestRepository;
import org.sadtech.haiti.core.service.AbstractFilterService;
import org.sadtech.haiti.filter.Filter;
import org.sadtech.haiti.filter.FilterQuery;
import org.sadtech.haiti.filter.criteria.CriteriaFilter;
import org.sadtech.haiti.filter.criteria.CriteriaQuery;
import org.springframework.stereotype.Service;

@Service
public class MergeRequestFilterService extends AbstractFilterService<MergeRequest, MergeRequestFilter> {

    public MergeRequestFilterService(MergeRequestRepository filterOperation) {
        super(filterOperation);
    }

    @Override
    protected Filter createFilter(@NonNull MergeRequestFilter filter) {
        return CriteriaFilter.<MergeRequest>create()
                .and(
                        convertFilter(filter)
                );
    }

    private FilterQuery convertFilter(@NonNull MergeRequestFilter filter) {
        return CriteriaQuery.<MergeRequest>create()
                .matchPhrase(MergeRequest_.ASSIGNEE, filter.getAssignee())
                .matchPhrase(MergeRequest_.STATE, filter.getStates());
    }

}
