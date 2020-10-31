package org.sadtech.bot.vcs.core.service.impl.filter;

import lombok.NonNull;
import org.sadtech.basic.core.service.AbstractFilterService;
import org.sadtech.basic.filter.Filter;
import org.sadtech.basic.filter.FilterQuery;
import org.sadtech.basic.filter.criteria.CriteriaFilter;
import org.sadtech.basic.filter.criteria.CriteriaQuery;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.PullRequest;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.PullRequest_;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.filter.PullRequestFilter;
import org.sadtech.bot.vsc.bitbucketbot.context.repository.PullRequestsRepository;
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
                .matchPhrase(PullRequest_.BITBUCKET_ID, filter.getBitbucketId())
                .matchPhrase(PullRequest_.REPOSITORY_ID, filter.getBitbucketRepositoryId());
    }

}
