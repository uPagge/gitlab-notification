package org.sadtech.bot.gitlab.core.service.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.entity.PullRequestMini;
import org.sadtech.bot.gitlab.context.domain.entity.Reviewer;
import org.sadtech.bot.gitlab.context.domain.filter.PullRequestFilter;
import org.sadtech.bot.gitlab.context.domain.notify.pullrequest.NewPrNotify;
import org.sadtech.bot.gitlab.context.repository.PullRequestsRepository;
import org.sadtech.bot.gitlab.context.service.MergeRequestsService;
import org.sadtech.bot.gitlab.context.service.NotifyService;
import org.sadtech.haiti.context.domain.ExistsContainer;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.core.service.AbstractSimpleManagerService;
import org.sadtech.haiti.core.util.Assert;
import org.sadtech.haiti.filter.FilterService;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

//@Service
public class MergeRequestsServiceImpl extends AbstractSimpleManagerService<MergeRequest, Long> implements MergeRequestsService {

    protected final NotifyService notifyService;
    protected final PullRequestsRepository pullRequestsRepository;
    protected final FilterService<MergeRequest, PullRequestFilter> filterService;


    protected MergeRequestsServiceImpl(
            PullRequestsRepository pullRequestsRepository,
            NotifyService notifyService,
            @Qualifier("pullRequestFilterService") FilterService<MergeRequest, PullRequestFilter> pullRequestsFilterService
    ) {
        super(pullRequestsRepository);
        this.notifyService = notifyService;
        this.pullRequestsRepository = pullRequestsRepository;
        this.filterService = pullRequestsFilterService;
    }

    @Override
    public MergeRequest create(@NonNull MergeRequest mergeRequest) {
        Assert.isNull(mergeRequest.getId(), "При создании идентификатор должен быть пустым");


        final MergeRequest newMergeRequest = pullRequestsRepository.save(mergeRequest);

        notifyService.send(
                NewPrNotify.builder()
                        .author(newMergeRequest.getAuthor().getName())
                        .description(newMergeRequest.getDescription())
                        .title(newMergeRequest.getTitle())
                        .url(newMergeRequest.getWebUrl())
                        .build()
        );

        return newMergeRequest;
    }

    @Override
    public MergeRequest update(@NonNull MergeRequest mergeRequest) {
        final MergeRequest oldMergeRequest = findAndFillId(mergeRequest);

        forgottenNotification(oldMergeRequest);

        oldMergeRequest.setTitle(mergeRequest.getTitle());
        oldMergeRequest.setDescription(mergeRequest.getDescription());
        updateReviewers(oldMergeRequest, mergeRequest);
        updateBitbucketVersion(oldMergeRequest, mergeRequest);
        updateStatus(oldMergeRequest, mergeRequest);
        updateConflict(oldMergeRequest, mergeRequest);

        return pullRequestsRepository.save(oldMergeRequest);
    }

    protected void forgottenNotification(MergeRequest mergeRequest) {
//        if (LocalDateTime.now().isAfter(mergeRequest.getUpdateDate().plusHours(2L))) {
//            final Set<String> smartReviewers = mergeRequest.getReviewers().stream()
//                    .filter(
//                            reviewer -> ReviewerStatus.NEEDS_WORK.equals(reviewer.getStatus())
//                                    && LocalDateTime.now().isAfter(reviewer.getDateChange().plusHours(2L))
//                                    && reviewer.getDateSmartNotify() == null
//                    )
//                    .peek(reviewer -> reviewer.setDateSmartNotify(LocalDateTime.now()))
//                    .map(Reviewer::getPersonLogin)
//                    .collect(Collectors.toSet());
//            if (!smartReviewers.isEmpty()) {
//                notifyService.send(
//                        ForgottenSmartPrNotify.builder()
//                                .projectKey(mergeRequest.getProjectKey())
//                                .repositorySlug(mergeRequest.getRepositorySlug())
//                                .recipients(smartReviewers)
//                                .title(mergeRequest.getTitle())
//                                .url(mergeRequest.getUrl())
//                                .build()
//                );
//            }
//        }
    }

    protected void updateBitbucketVersion(MergeRequest oldMergeRequest, MergeRequest mergeRequest) {
//        if (
//                !oldMergeRequest.getBitbucketVersion().equals(mergeRequest.getBitbucketVersion())
//        ) {
//            oldMergeRequest.setBitbucketVersion(mergeRequest.getBitbucketVersion());
//            if (PullRequestStatus.OPEN.equals(mergeRequest.getStatus())) {
//                notifyService.send(
//                        UpdatePrNotify.builder()
//                                .author(oldMergeRequest.getAuthorLogin())
//                                .name(mergeRequest.getTitle())
//                                .recipients(
//                                        mergeRequest.getReviewers().stream()
//                                                .map(Reviewer::getPersonLogin)
//                                                .collect(Collectors.toSet())
//                                )
//                                .url(oldMergeRequest.getUrl())
//                                .projectKey(oldMergeRequest.getProjectKey())
//                                .repositorySlug(oldMergeRequest.getRepositorySlug())
//                                .build()
//                );
//            }
//        }
    }

    protected void updateConflict(MergeRequest oldMergeRequest, MergeRequest mergeRequest) {
//        if (!oldMergeRequest.isConflict() && mergeRequest.isConflict()) {
//            notifyService.send(
//                    ConflictPrNotify.builder()
//                            .name(mergeRequest.getTitle())
//                            .url(mergeRequest.getUrl())
//                            .projectKey(mergeRequest.getProjectKey())
//                            .repositorySlug(mergeRequest.getRepositorySlug())
//                            .recipients(Collections.singleton(mergeRequest.getAuthorLogin()))
//                            .build()
//            );
//        }
//        oldMergeRequest.setConflict(mergeRequest.isConflict());
    }

    protected void updateStatus(MergeRequest oldMergeRequest, MergeRequest newMergeRequest) {
//        final PullRequestStatus oldStatus = oldMergeRequest.getStatus();
//        final PullRequestStatus newStatus = newMergeRequest.getStatus();
//        if (!oldStatus.equals(newStatus)) {
//            notifyService.send(
//                    StatusPrNotify.builder()
//                            .name(newMergeRequest.getTitle())
//                            .url(oldMergeRequest.getUrl())
//                            .projectKey(oldMergeRequest.getProjectKey())
//                            .repositorySlug(oldMergeRequest.getRepositorySlug())
//                            .newStatus(newStatus)
//                            .oldStatus(oldStatus)
//                            .recipients(Collections.singleton(oldMergeRequest.getAuthorLogin()))
//                            .build()
//            );
//            oldMergeRequest.setStatus(newStatus);
//        }
    }

    protected void updateReviewers(MergeRequest oldMergeRequest, MergeRequest newMergeRequest) {
//        final Map<String, Reviewer> oldReviewers = oldMergeRequest.getReviewers().stream()
//                .collect(Collectors.toMap(Reviewer::getPersonLogin, reviewer -> reviewer));
//        final Map<String, Reviewer> newReviewers = newMergeRequest.getReviewers().stream()
//                .collect(Collectors.toMap(Reviewer::getPersonLogin, reviewer -> reviewer));
//        final List<ReviewerChange> reviewerChanges = new ArrayList<>();
//        for (Reviewer newReviewer : newReviewers.values()) {
//            if (oldReviewers.containsKey(newReviewer.getPersonLogin())) {
//                final Reviewer oldReviewer = oldReviewers.get(newReviewer.getPersonLogin());
//                final ReviewerStatus oldStatus = oldReviewer.getStatus();
//                final ReviewerStatus newStatus = newReviewer.getStatus();
//                if (!oldStatus.equals(newStatus)) {
//                    reviewerChanges.add(ReviewerChange.ofOld(oldReviewer.getPersonLogin(), oldStatus, newStatus));
//                    oldReviewer.setStatus(newStatus);
//                    oldReviewer.setDateChange(LocalDateTime.now());
//                    smartNotifyAfterReviewerDecision(newReviewer, oldMergeRequest);
//                }
//            } else {
//                reviewerChanges.add(ReviewerChange.ofNew(newReviewer.getPersonLogin(), newReviewer.getStatus()));
//                newReviewer.setMergeRequest(oldMergeRequest);
//                newReviewer.setDateChange(LocalDateTime.now());
//                oldMergeRequest.getReviewers().add(newReviewer);
//            }
//        }
//        final Set<String> oldIds = oldReviewers.keySet();
//        oldIds.removeAll(newReviewers.keySet());
//        reviewerChanges.addAll(
//                oldReviewers.entrySet().stream()
//                        .filter(e -> oldIds.contains(e.getKey()))
//                        .map(e -> ReviewerChange.ofDeleted(e.getValue().getPersonLogin()))
//                        .collect(Collectors.toList())
//        );
//        oldMergeRequest.getReviewers()
//                .removeIf(reviewer -> oldIds.contains(reviewer.getPersonLogin()));
//        if (!reviewerChanges.isEmpty()) {
//            notifyService.send(
//                    ReviewersPrNotify.builder()
//                            .title(newMergeRequest.getTitle())
//                            .url(newMergeRequest.getUrl())
//                            .projectKey(newMergeRequest.getProjectKey())
//                            .repositorySlug(newMergeRequest.getRepositorySlug())
//                            .recipients(Collections.singleton(newMergeRequest.getAuthorLogin()))
//                            .reviewerChanges(reviewerChanges)
//                            .build()
//            );
//        }
    }

    /**
     * Умное уведомление ревьюверов, после того, как кто-то изменил свое решение.
     */
    protected void smartNotifyAfterReviewerDecision(Reviewer newReviewer, MergeRequest oldMergeRequest) {
//        final ReviewerStatus newStatus = newReviewer.getStatus();
//        if (!ReviewerStatus.NEEDS_WORK.equals(newStatus) && enoughTimHasPassedSinceUpdatePr(oldMergeRequest.getUpdateDate())) {
//            final List<Reviewer> smartReviewers = oldMergeRequest.getReviewers().stream()
//                    .filter(reviewer -> LocalDateTime.now().isAfter(reviewer.getDateChange().plusHours(2L)))
//                    .collect(Collectors.toList());
//            if (!smartReviewers.isEmpty()) {
//                notifyService.send(
//                        SmartPrNotify.builder()
//                                .reviewerTriggered(newReviewer)
//                                .title(oldMergeRequest.getTitle())
//                                .url(oldMergeRequest.getUrl())
//                                .projectKey(oldMergeRequest.getProjectKey())
//                                .repositorySlug(oldMergeRequest.getRepositorySlug())
//                                .recipients(
//                                        smartReviewers.stream()
//                                                .map(Reviewer::getPersonLogin)
//                                                .collect(Collectors.toSet())
//                                )
//                                .build()
//                );
//            }
//        }
    }

    protected boolean enoughTimHasPassedSinceUpdatePr(LocalDateTime updateDate) {
        return LocalDateTime.now().isAfter(updateDate.plusHours(4L));
    }

    @Override
    public Set<IdAndStatusPr> getAllId(Set<MergeRequestState> statuses) {
        return null;
    }

    @Override
    public Optional<PullRequestMini> getMiniInfo(@NonNull Long pullRequestId) {
        return pullRequestsRepository.findMiniInfoById(pullRequestId);
    }

    @Override
    public Sheet<MergeRequest> getAll(@NonNull PullRequestFilter filter, Pagination pagination) {
        return filterService.getAll(filter, pagination);
    }

    @Override
    public Optional<MergeRequest> getFirst(@NonNull PullRequestFilter pullRequestFilter) {
        return filterService.getFirst(pullRequestFilter);
    }

    @Override
    public boolean exists(@NonNull PullRequestFilter filter) {
        return filterService.exists(filter);
    }

    @Override
    public long count(@NonNull PullRequestFilter pullRequestFilter) {
        return filterService.count(pullRequestFilter);
    }

    protected MergeRequest findAndFillId(@NonNull MergeRequest mergeRequest) {
//        return pullRequestsRepository.findFirst(
//                CriteriaFilter.create().and(
//                        CriteriaQuery.create()
//                                .matchPhrase("hyita", mergeRequest.getBitbucketId())
//                                .matchPhrase("hyita", mergeRequest.getRepositoryId())
//                )
//        ).orElseThrow(() -> new UpdateException("ПР с таким id не существует"));
        return null;
    }

    @Override
    public ExistsContainer<MergeRequest, Long> existsById(@NonNull Collection<Long> collection) {
        return null;
    }

}
