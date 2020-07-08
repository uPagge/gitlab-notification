package org.sadtech.bot.bitbucketbot.service.impl.filter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.basic.context.page.Pagination;
import org.sadtech.basic.context.page.Sheet;
import org.sadtech.basic.context.service.simple.FilterService;
import org.sadtech.basic.filter.Filter;
import org.sadtech.basic.filter.FilterQuery;
import org.sadtech.basic.filter.criteria.CriteriaFilter;
import org.sadtech.basic.filter.criteria.CriteriaQuery;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest_;
import org.sadtech.bot.bitbucketbot.domain.filter.PullRequestFilter;
import org.sadtech.bot.bitbucketbot.repository.PullRequestsRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PullRequestFilterService implements FilterService<PullRequest, PullRequestFilter> {

    private final PullRequestsRepository pullRequestsRepository;

    @Override
    public Sheet<PullRequest> getAll(@NonNull PullRequestFilter filter, Pagination pagination) {
        return pullRequestsRepository.findAll(createFilter(filter), pagination);
    }

    @Override
    public List<PullRequest> getAll(@NonNull PullRequestFilter filter) {
        return pullRequestsRepository.findAll(createFilter(filter));
    }

    @Override
    public Optional<PullRequest> getFirst(@NonNull PullRequestFilter filter) {
        return pullRequestsRepository.findFirst(createFilter(filter));
    }

    @Override
    public boolean exists(@NonNull PullRequestFilter filter) {
        return pullRequestsRepository.exists(createFilter(filter));
    }

    private FilterQuery convertFilter(@NonNull PullRequestFilter filter) {
        return CriteriaQuery.<PullRequest>create()
                .matchPhrase(PullRequest_.BITBUCKET_ID, filter.getBitbucketId())
                .matchPhrase(PullRequest_.REPOSITORY_ID, filter.getBitbucketRepositoryId());
    }

    private Filter createFilter(PullRequestFilter filter) {
        return CriteriaFilter.<PullRequest>create()
                .and(
                        convertFilter(filter)
                );
    }

}
