package org.sadtech.bot.gitlab.core.service.impl.filter;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.PullRequest;
import org.sadtech.bot.gitlab.context.domain.filter.PullRequestFilter;
import org.sadtech.bot.gitlab.context.repository.PullRequestsRepository;
import org.sadtech.haiti.core.service.AbstractFilterService;
import org.sadtech.haiti.filter.Filter;
import org.sadtech.haiti.filter.FilterQuery;
import org.sadtech.haiti.filter.criteria.CriteriaFilter;
import org.sadtech.haiti.filter.criteria.CriteriaQuery;
import org.springframework.stereotype.Service;

@Service
public class PullRequestFilterService extends AbstractFilterService<PullRequest, PullRequestFilter> {

    public PullRequestFilterService(PullRequestsRepository filterOperation) {
        super(filterOperation);
    }

    @Override
    protected Filter createFilter(@NonNull PullRequestFilter filter) {
        return CriteriaFilter.<PullRequest>create()
                .and(
                        convertFilter(filter)
                );
    }

    private FilterQuery convertFilter(@NonNull PullRequestFilter filter) {
        return CriteriaQuery.<PullRequest>create()
                .matchPhrase("hyita", filter.getBitbucketId())
                .matchPhrase("hyita", filter.getBitbucketRepositoryId());
    }
}
