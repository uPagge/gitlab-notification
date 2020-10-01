package org.sadtech.bot.vcs.core.service.impl;

import lombok.NonNull;
import org.sadtech.basic.context.page.Pagination;
import org.sadtech.basic.context.page.Sheet;
import org.sadtech.basic.context.service.simple.FilterService;
import org.sadtech.basic.core.service.AbstractSimpleManagerService;
import org.sadtech.basic.core.util.Assert;
import org.sadtech.basic.filter.criteria.CriteriaFilter;
import org.sadtech.basic.filter.criteria.CriteriaQuery;
import org.sadtech.bot.vcs.core.domain.IdAndStatusPr;
import org.sadtech.bot.vcs.core.domain.PointType;
import org.sadtech.bot.vcs.core.domain.PullRequestStatus;
import org.sadtech.bot.vcs.core.domain.ReviewerStatus;
import org.sadtech.bot.vcs.core.domain.entity.PullRequest;
import org.sadtech.bot.vcs.core.domain.entity.PullRequestMini;
import org.sadtech.bot.vcs.core.domain.entity.PullRequest_;
import org.sadtech.bot.vcs.core.domain.entity.Reviewer;
import org.sadtech.bot.vcs.core.domain.filter.PullRequestFilter;
import org.sadtech.bot.vcs.core.domain.notify.pullrequest.ConflictPrNotify;
import org.sadtech.bot.vcs.core.domain.notify.pullrequest.NewPrNotify;
import org.sadtech.bot.vcs.core.domain.notify.pullrequest.ReviewersPrNotify;
import org.sadtech.bot.vcs.core.domain.notify.pullrequest.StatusPrNotify;
import org.sadtech.bot.vcs.core.domain.notify.pullrequest.UpdatePrNotify;
import org.sadtech.bot.vcs.core.domain.util.ReviewerChange;
import org.sadtech.bot.vcs.core.exception.UpdateException;
import org.sadtech.bot.vcs.core.repository.PullRequestsRepository;
import org.sadtech.bot.vcs.core.service.NotifyService;
import org.sadtech.bot.vcs.core.service.PullRequestsService;
import org.sadtech.bot.vcs.core.service.RatingService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PullRequestsServiceImpl extends AbstractSimpleManagerService<PullRequest, Long> implements PullRequestsService {

    private final NotifyService notifyService;
    private final PullRequestsRepository pullRequestsRepository;
    private final RatingService ratingService;
    private final FilterService<PullRequest, PullRequestFilter> filterService;

    protected PullRequestsServiceImpl(
            PullRequestsRepository pullRequestsRepository,
            NotifyService notifyService,
            RatingService ratingService,
            @Qualifier("pullRequestFilterService") FilterService<PullRequest, PullRequestFilter> pullRequestsFilterService
    ) {
        super(pullRequestsRepository);
        this.notifyService = notifyService;
        this.pullRequestsRepository = pullRequestsRepository;
        this.ratingService = ratingService;
        this.filterService = pullRequestsFilterService;
    }

    @Override
    public PullRequest create(@NonNull PullRequest pullRequest) {
        Assert.isNull(pullRequest.getId(), "При создании идентификатор должен быть пустым");

        final PullRequest newPullRequest = pullRequestsRepository.save(pullRequest);

        ratingService.addRating(newPullRequest.getAuthorLogin(), PointType.CREATE_PULL_REQUEST, PointType.CREATE_PULL_REQUEST.getPoints());
        notifyService.send(
                NewPrNotify.builder()
                        .author(newPullRequest.getAuthorLogin())
                        .description(newPullRequest.getDescription())
                        .title(newPullRequest.getTitle())
                        .url(newPullRequest.getUrl())
                        .logins(
                                newPullRequest.getReviewers().stream()
                                        .map(Reviewer::getPersonLogin)
                                        .collect(Collectors.toSet())
                        )
                        .build()
        );

        return newPullRequest;
    }

    @Override
    public PullRequest update(@NonNull PullRequest pullRequest) {
        final PullRequest oldPullRequest = findAndFillId(pullRequest);

        oldPullRequest.setBitbucketVersion(pullRequest.getBitbucketVersion());
        oldPullRequest.setTitle(pullRequest.getTitle());
        oldPullRequest.setDescription(pullRequest.getDescription());
        updateReviewers(oldPullRequest, pullRequest);
        updateStatus(oldPullRequest, pullRequest);
        updateConflict(oldPullRequest, pullRequest);

        final PullRequest newPullRequest = pullRequestsRepository.save(oldPullRequest);
        if (!pullRequest.getBitbucketVersion().equals(newPullRequest.getBitbucketVersion())) {
            notifyService.send(
                    UpdatePrNotify.builder()
                            .author(oldPullRequest.getAuthorLogin())
                            .name(newPullRequest.getTitle())
                            .logins(
                                    newPullRequest.getReviewers().stream()
                                            .map(Reviewer::getPersonLogin)
                                            .collect(Collectors.toSet())
                            )
                            .url(newPullRequest.getUrl())
                            .build()
            );
        }

        return newPullRequest;
    }

    private void updateConflict(PullRequest oldPullRequest, PullRequest pullRequest) {
        if (!oldPullRequest.isConflict() && pullRequest.isConflict()) {
            notifyService.send(
                    ConflictPrNotify.builder()
                            .name(pullRequest.getTitle())
                            .url(pullRequest.getUrl())
                            .logins(Collections.singleton(pullRequest.getAuthorLogin()))
                            .build()
            );
        }
        oldPullRequest.setConflict(pullRequest.isConflict());
    }

    private void updateStatus(PullRequest oldPullRequest, PullRequest newPullRequest) {
        final PullRequestStatus oldStatus = oldPullRequest.getStatus();
        final PullRequestStatus newStatus = newPullRequest.getStatus();
        if (!oldStatus.equals(newStatus)) {
            ratingStatus(oldPullRequest, newPullRequest);
            notifyService.send(
                    StatusPrNotify.builder()
                            .name(newPullRequest.getTitle())
                            .url(oldPullRequest.getUrl())
                            .newStatus(newStatus)
                            .oldStatus(oldStatus)
                            .logins(Collections.singleton(oldPullRequest.getAuthorLogin()))
                            .build()
            );
            oldPullRequest.setStatus(newStatus);
        }
    }

    private void ratingStatus(PullRequest oldPullRequest, PullRequest newPullRequest) {
        final String authorLogin = oldPullRequest.getAuthorLogin();
        switch (newPullRequest.getStatus()) {
            case OPEN:
                ratingService.addRating(authorLogin, PointType.CREATE_PULL_REQUEST, PointType.CREATE_PULL_REQUEST.getPoints());
                break;
            case MERGED:
                // TODO: 01.10.2020 Нужно продумать как расчитывать баллы при мерже.
                break;
            case DECLINED:
                ratingService.addRating(authorLogin, PointType.DECLINE_PULL_REQUEST, PointType.DECLINE_PULL_REQUEST.getPoints());
                break;
        }
    }

    private void updateReviewers(PullRequest oldPullRequest, PullRequest newPullRequest) {
        final Map<String, Reviewer> oldReviewers = oldPullRequest.getReviewers().stream()
                .collect(Collectors.toMap(Reviewer::getPersonLogin, reviewer -> reviewer));
        final Map<String, Reviewer> newReviewers = newPullRequest.getReviewers().stream()
                .collect(Collectors.toMap(Reviewer::getPersonLogin, reviewer -> reviewer));
        final List<ReviewerChange> reviewerChanges = new ArrayList<>();
        for (Reviewer newReviewer : newReviewers.values()) {
            if (oldReviewers.containsKey(newReviewer.getPersonLogin())) {
                final Reviewer oldReviewer = oldReviewers.get(newReviewer.getPersonLogin());
                final ReviewerStatus oldStatus = oldReviewer.getStatus();
                final ReviewerStatus newStatus = newReviewer.getStatus();
                if (!oldStatus.equals(newStatus)) {
                    reviewerChanges.add(ReviewerChange.ofOld(oldReviewer.getPersonLogin(), oldStatus, newStatus));
                    oldReviewer.setStatus(newStatus);
                }
            } else {
                reviewerChanges.add(ReviewerChange.ofNew(newReviewer.getPersonLogin(), newReviewer.getStatus()));
                newReviewer.setPullRequest(oldPullRequest);
                oldPullRequest.getReviewers().add(newReviewer);
            }
        }
        final Set<String> oldIds = oldReviewers.keySet();
        oldIds.removeAll(newReviewers.keySet());
        reviewerChanges.addAll(
                oldReviewers.entrySet().stream()
                        .filter(e -> oldIds.contains(e.getKey()))
                        .map(e -> ReviewerChange.ofDeleted(e.getValue().getPersonLogin()))
                        .collect(Collectors.toList())
        );
        oldPullRequest.getReviewers()
                .removeIf(reviewer -> oldIds.contains(reviewer.getPersonLogin()));
        if (!reviewerChanges.isEmpty()) {
            notifyService.send(
                    ReviewersPrNotify.builder()
                            .title(newPullRequest.getTitle())
                            .url(newPullRequest.getUrl())
                            .logins(Collections.singleton(newPullRequest.getAuthorLogin()))
                            .reviewerChanges(reviewerChanges)
                            .build()
            );
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
    public Optional<PullRequestMini> getMiniInfo(@NonNull Long pullRequestId) {
        return pullRequestsRepository.findMiniInfoById(pullRequestId);
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
