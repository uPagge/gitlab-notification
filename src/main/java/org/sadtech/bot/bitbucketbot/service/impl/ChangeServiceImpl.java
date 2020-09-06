package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.domain.ReviewerStatus;
import org.sadtech.bot.bitbucketbot.domain.change.Change;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.NewPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.ReviewersPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.UpdatePrChange;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.entity.Reviewer;
import org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange;
import org.sadtech.bot.bitbucketbot.repository.ChangeRepository;
import org.sadtech.bot.bitbucketbot.service.ChangeService;
import org.sadtech.bot.bitbucketbot.service.PersonService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChangeServiceImpl implements ChangeService {

    private final ChangeRepository changeRepository;
    private final PersonService personService;

    @Override
    public NewPrChange create(@NonNull PullRequest newPullRequest) {
        return changeRepository.add(
                NewPrChange.builder()
                        .author(newPullRequest.getAuthorLogin())
                        .description(newPullRequest.getDescription())
                        .title(newPullRequest.getTitle())
                        .url(newPullRequest.getUrl())
                        .telegramIds(getReviewerTelegrams(newPullRequest.getReviewers()))
                        .build()
        );
    }

    @Override
    public UpdatePrChange createUpdatePr(@NonNull PullRequest oldPullRequest, @NonNull PullRequest newPullRequest) {
        return changeRepository.add(
                UpdatePrChange.builder()
                        .author(oldPullRequest.getAuthorLogin())
                        .name(newPullRequest.getTitle())
                        .telegramIds(getReviewerTelegrams(newPullRequest.getReviewers()))
                        .url(newPullRequest.getUrl())
                        .build()
        );
    }

    @Override
    public Change createReviewersPr(@NonNull PullRequest oldPullRequest, @NonNull PullRequest newPullRequest) {
        final Map<Long, Reviewer> oldReviewers = oldPullRequest.getReviewers().stream()
                .collect(Collectors.toMap(Reviewer::getId, reviewer -> reviewer));
        final Map<Long, Reviewer> newReviewers = newPullRequest.getReviewers().stream()
                .collect(Collectors.toMap(Reviewer::getId, reviewer -> reviewer));
        final List<ReviewerChange> reviewerChanges = new ArrayList<>();
        for (Reviewer newReviewer : newReviewers.values()) {
            if (oldReviewers.containsKey(newReviewer.getId())) {
                final Reviewer oldReviewer = oldReviewers.get(newReviewer.getId());
                final ReviewerStatus oldStatus = oldReviewer.getStatus();
                final ReviewerStatus newStatus = newReviewer.getStatus();
                if (!oldStatus.equals(newStatus)) {
                    reviewerChanges.add(ReviewerChange.ofOld(oldReviewer.getUserLogin(), oldStatus, newStatus));
                }
            } else {
                reviewerChanges.add(ReviewerChange.ofNew(newReviewer.getUserLogin(), newReviewer.getStatus()));
            }
        }
        final Set<Long> oldIds = oldReviewers.keySet();
        oldIds.removeAll(newReviewers.keySet());
        reviewerChanges.addAll(
                oldReviewers.entrySet().stream()
                        .filter(e -> oldIds.contains(e.getKey()))
                        .map(e -> ReviewerChange.ofDeleted(e.getValue().getUserLogin()))
                        .collect(Collectors.toList())
        );
        return changeRepository.add(
                ReviewersPrChange.builder()
                        .title(newPullRequest.getTitle())
                        .url(newPullRequest.getUrl())
                        .telegramId(personService.getTelegramIdByLogin(newPullRequest.getAuthorLogin()).orElse(null))
                        .reviewerChanges(reviewerChanges)
                        .build()
        );
    }

    private Set<Long> getReviewerTelegrams(@NonNull List<Reviewer> reviewers) {
        return personService.getAllTelegramIdByLogin(
                reviewers.stream()
                        .map(Reviewer::getUserLogin)
                        .collect(Collectors.toSet())
        );
    }

    @Override
    public List<Change> getNew() {
        final List<Change> changes = changeRepository.getAll();
        changeRepository.deleteAll(changes);
        return changes;
    }


}
