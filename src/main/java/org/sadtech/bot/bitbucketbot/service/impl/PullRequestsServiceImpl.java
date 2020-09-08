package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import org.sadtech.basic.context.page.Pagination;
import org.sadtech.basic.context.page.Sheet;
import org.sadtech.basic.context.service.simple.FilterService;
import org.sadtech.basic.core.service.AbstractSimpleManagerService;
import org.sadtech.basic.core.util.Assert;
import org.sadtech.basic.filter.criteria.CriteriaFilter;
import org.sadtech.basic.filter.criteria.CriteriaQuery;
import org.sadtech.bot.bitbucketbot.domain.IdAndStatusPr;
import org.sadtech.bot.bitbucketbot.domain.PullRequestStatus;
import org.sadtech.bot.bitbucketbot.domain.ReviewerStatus;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest_;
import org.sadtech.bot.bitbucketbot.domain.entity.Reviewer;
import org.sadtech.bot.bitbucketbot.domain.filter.PullRequestFilter;
import org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange;
import org.sadtech.bot.bitbucketbot.exception.UpdateException;
import org.sadtech.bot.bitbucketbot.repository.PullRequestsRepository;
import org.sadtech.bot.bitbucketbot.service.ChangeService;
import org.sadtech.bot.bitbucketbot.service.PullRequestsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PullRequestsServiceImpl extends AbstractSimpleManagerService<PullRequest, Long> implements PullRequestsService {

    private final ChangeService changeService;
    private final PullRequestsRepository pullRequestsRepository;
    private final FilterService<PullRequest, PullRequestFilter> filterService;

    protected PullRequestsServiceImpl(
            PullRequestsRepository pullRequestsRepository,
            ChangeService changeService,
            @Qualifier("pullRequestFilterService") FilterService<PullRequest, PullRequestFilter> pullRequestsFilterService
    ) {
        super(pullRequestsRepository);
        this.changeService = changeService;
        this.pullRequestsRepository = pullRequestsRepository;
        this.filterService = pullRequestsFilterService;
    }

    @Override
    public PullRequest create(@NonNull PullRequest pullRequest) {
        Assert.isNull(pullRequest.getId(), "При создании идентификатор должен быть пустым");

        final PullRequest newPullRequest = pullRequestsRepository.save(pullRequest);

        changeService.create(newPullRequest);

        return newPullRequest;
    }

    @Override
    public PullRequest update(@NonNull PullRequest pullRequest) {
        final PullRequest oldPullRequest = findAndFillId(pullRequest);
        oldPullRequest.setBitbucketVersion(pullRequest.getBitbucketVersion());
        oldPullRequest.setConflict(pullRequest.isConflict());
        oldPullRequest.setTitle(pullRequest.getTitle());
        oldPullRequest.setDescription(pullRequest.getDescription());
        oldPullRequest.setStatus(pullRequest.getStatus());
        updateReviewers(oldPullRequest, pullRequest);

        final PullRequest newPullRequest = pullRequestsRepository.save(oldPullRequest);
        if (!pullRequest.getBitbucketVersion().equals(newPullRequest.getBitbucketVersion())) {
            changeService.createUpdatePr(pullRequest, newPullRequest);
        }

        return newPullRequest;
    }

    private void updateReviewers(PullRequest oldPullRequest, PullRequest newPullRequest) {
        final Map<String, Reviewer> oldReviewers = oldPullRequest.getReviewers().stream()
                .collect(Collectors.toMap(Reviewer::getUserLogin, reviewer -> reviewer));
        final Map<String, Reviewer> newReviewers = newPullRequest.getReviewers().stream()
                .collect(Collectors.toMap(Reviewer::getUserLogin, reviewer -> reviewer));
        final List<ReviewerChange> reviewerChanges = new ArrayList<>();
        for (Reviewer newReviewer : newReviewers.values()) {
            if (oldReviewers.containsKey(newReviewer.getUserLogin())) {
                final Reviewer oldReviewer = oldReviewers.get(newReviewer.getUserLogin());
                final ReviewerStatus oldStatus = oldReviewer.getStatus();
                final ReviewerStatus newStatus = newReviewer.getStatus();
                if (!oldStatus.equals(newStatus)) {
                    reviewerChanges.add(ReviewerChange.ofOld(oldReviewer.getUserLogin(), oldStatus, newStatus));
                    oldReviewer.setStatus(newStatus);
                }
            } else {
                reviewerChanges.add(ReviewerChange.ofNew(newReviewer.getUserLogin(), newReviewer.getStatus()));
                newReviewer.setPullRequest(oldPullRequest);
                oldPullRequest.getReviewers().add(newReviewer);
            }
        }
        final Set<String> oldIds = oldReviewers.keySet();
        oldIds.removeAll(newReviewers.keySet());
        reviewerChanges.addAll(
                oldReviewers.entrySet().stream()
                        .filter(e -> oldIds.contains(e.getKey()))
                        .map(e -> ReviewerChange.ofDeleted(e.getValue().getUserLogin()))
                        .collect(Collectors.toList())
        );
        oldPullRequest.getReviewers()
                .removeIf(reviewer -> oldIds.contains(reviewer.getUserLogin()));
        if (!reviewerChanges.isEmpty()) {
            changeService.createReviewersPr(newPullRequest.getTitle(), newPullRequest.getUrl(), newPullRequest.getAuthorLogin(), reviewerChanges);
        }
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
    public Sheet<PullRequest> getAll(@NonNull PullRequestFilter filter, Pagination pagination) {
        return filterService.getAll(filter, pagination);
    }

    @Override
    public List<PullRequest> getAll(@NonNull PullRequestFilter filter) {
        return filterService.getAll(filter);
    }

    @Override
    public Optional<PullRequest> getFirst(@NonNull PullRequestFilter filter) {
        return filterService.getFirst(filter);
    }

    @Override
    public boolean exists(@NonNull PullRequestFilter filter) {
        return filterService.exists(filter);
    }

    private PullRequest findAndFillId(@NonNull PullRequest pullRequest) {
        return pullRequestsRepository.findFirst(
                CriteriaFilter.create().and(
                        CriteriaQuery.create()
                                .matchPhrase(PullRequest_.BITBUCKET_ID, pullRequest.getBitbucketId())
                                .matchPhrase(PullRequest_.REPOSITORY_ID, pullRequest.getRepositoryId())
                )
        ).orElseThrow(() -> new UpdateException("ПР с таким id не существует"));
    }

}
