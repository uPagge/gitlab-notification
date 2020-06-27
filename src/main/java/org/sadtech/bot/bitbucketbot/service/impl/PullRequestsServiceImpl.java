package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import org.sadtech.basic.context.page.Pagination;
import org.sadtech.basic.context.page.Sheet;
import org.sadtech.basic.core.service.AbstractBusinessLogicService;
import org.sadtech.basic.filter.criteria.CriteriaQuery;
import org.sadtech.bot.bitbucketbot.domain.IdAndStatusPr;
import org.sadtech.bot.bitbucketbot.domain.PullRequestStatus;
import org.sadtech.bot.bitbucketbot.domain.ReviewerStatus;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest_;
import org.sadtech.bot.bitbucketbot.domain.filter.PullRequestFilter;
import org.sadtech.bot.bitbucketbot.exception.CreateException;
import org.sadtech.bot.bitbucketbot.exception.UpdateException;
import org.sadtech.bot.bitbucketbot.repository.PullRequestsRepository;
import org.sadtech.bot.bitbucketbot.service.ChangeService;
import org.sadtech.bot.bitbucketbot.service.PullRequestsService;
import org.sadtech.bot.bitbucketbot.utils.ChangeGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PullRequestsServiceImpl extends AbstractBusinessLogicService<PullRequest, Long> implements PullRequestsService {

    private final ChangeService changeService;
    private final PullRequestsRepository pullRequestsRepository;

    protected PullRequestsServiceImpl(PullRequestsRepository pullRequestsRepository, ChangeService changeService) {
        super(pullRequestsRepository);
        this.changeService = changeService;
        this.pullRequestsRepository = pullRequestsRepository;
    }

    @Override
    public PullRequest create(@NonNull PullRequest pullRequest) {
        if (pullRequest.getId() == null) {
            final PullRequest newPullRequest = pullRequestsRepository.save(pullRequest);
            changeService.add(ChangeGenerator.create(newPullRequest));
            return newPullRequest;
        }
        throw new CreateException("При создании идентификатор должен быть пустым");
    }

    @Override
    public PullRequest update(@NonNull PullRequest pullRequest) {
        final PullRequest oldPullRequest = findAndFillId(pullRequest);

        if (!oldPullRequest.getBitbucketVersion().equals(pullRequest.getBitbucketVersion())) {
            oldPullRequest.setBitbucketVersion(pullRequest.getVersion());
            oldPullRequest.setConflict(pullRequest.isConflict());
            oldPullRequest.setTitle(pullRequest.getTitle());
            oldPullRequest.setDescription(pullRequest.getDescription());
            oldPullRequest.setStatus(pullRequest.getStatus());
            oldPullRequest.setReviewers(pullRequest.getReviewers());

            final PullRequest newPullRequest = pullRequestsRepository.save(oldPullRequest);

            changeService.add(ChangeGenerator.createUpdatePr(pullRequest, newPullRequest));
            changeService.add(ChangeGenerator.createReviewersPr(pullRequest, newPullRequest));

            return newPullRequest;
        }
        return oldPullRequest;
    }

    private PullRequest findAndFillId(@NonNull PullRequest pullRequest) {
        return pullRequestsRepository.findByFilterQuery(
                CriteriaQuery.create()
                        .matchPhrase(PullRequest_.BITBUCKET_ID, pullRequest.getBitbucketId())
                        .matchPhrase(PullRequest_.REPOSITORY_ID, pullRequest.getRepositoryId())
        ).orElseThrow(() -> new UpdateException("ПР с таким id не существует"));
    }

    @NonNull
    @Override
    public List<PullRequest> getAllByReviewerAndStatuses(String login, ReviewerStatus reviewerStatus, Set<PullRequestStatus> statuses) {
        return pullRequestsRepository.findAllByReviewerAndStatuses(login, reviewerStatus, statuses);
    }

    @Override
    public List<PullRequest> getAllByAuthorAndReviewerStatus(@NonNull String login, @NonNull ReviewerStatus status) {
        return pullRequestsRepository.findAllByAuthorAndReviewerStatus(login, status);
    }

    @Override
    public Set<IdAndStatusPr> getAllId(Set<PullRequestStatus> statuses) {
        return pullRequestsRepository.findAllIdByStatusIn(statuses);
    }

    @Override
    public Sheet<PullRequest> getAllByFilter(@NonNull PullRequestFilter filter, Pagination pagination) {
        return null;
    }

    @Override
    public Sheet<PullRequest> getALlByFilterQuery(@NonNull PullRequestFilter filter, Pagination pagination) {
        return null;
    }

    @Override
    public Optional<PullRequest> getByFilterQuery(@NonNull PullRequestFilter filterQuery) {
        return Optional.empty();
    }

    @Override
    public boolean existsByFilterQuery(@NonNull PullRequestFilter filter) {
        return pullRequestsRepository.existsByFilterQuery(
                CriteriaQuery.<PullRequest>create()
                        .matchPhrase(PullRequest_.BITBUCKET_ID, filter.getBitbucketId())
                        .matchPhrase(PullRequest_.REPOSITORY_ID, filter.getBitbucketRepositoryId())
        );
    }

}
