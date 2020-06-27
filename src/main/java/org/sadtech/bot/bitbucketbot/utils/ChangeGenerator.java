package org.sadtech.bot.bitbucketbot.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.ReviewerStatus;
import org.sadtech.bot.bitbucketbot.domain.change.Change;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.NewPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.ReviewersPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.UpdatePrChange;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.entity.Reviewer;
import org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChangeGenerator {

    public static NewPrChange create(@NonNull PullRequest newPullRequest) {
        return NewPrChange.builder()
                .author(newPullRequest.getAuthor().getFullName())
                .description(newPullRequest.getDescription())
                .title(newPullRequest.getTitle())
                .url(newPullRequest.getUrl())
                .telegramIds(
                        newPullRequest.getReviewers().stream()
                                .map(reviewer -> reviewer.getUser().getTelegramId())
                                .collect(Collectors.toSet())
                )
                .build();
    }

    public static UpdatePrChange createUpdatePr(@NonNull PullRequest oldPullRequest, @NonNull PullRequest newPullRequest) {
        return UpdatePrChange.builder()
                .author(oldPullRequest.getAuthor().getFullName())
                .name(newPullRequest.getAuthor().getFullName())
                .telegramIds(
                        newPullRequest.getReviewers().stream()
                                .map(reviewer -> reviewer.getUser().getTelegramId())
                                .collect(Collectors.toSet())
                )
                .url(newPullRequest.getUrl())
                .build();
    }

    public static Change createReviewersPr(@NonNull PullRequest oldPullRequest, @NonNull PullRequest newPullRequest) {
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
                    reviewerChanges.add(ReviewerChange.ofOld(oldReviewer.getUser().getFullName(), oldStatus, newStatus));
                }
            } else {
                reviewerChanges.add(ReviewerChange.ofNew(newReviewer.getUser().getFullName(), newReviewer.getStatus()));
            }
        }
        final Set<Long> oldIds = oldReviewers.keySet();
        oldIds.removeAll(newReviewers.keySet());
        reviewerChanges.addAll(
                oldReviewers.entrySet().stream()
                        .filter(e -> oldIds.contains(e.getKey()))
                        .map(e -> ReviewerChange.ofDeleted(e.getValue().getUser().getFullName()))
                        .collect(Collectors.toList())
        );
        return ReviewersPrChange.builder()
                .title(newPullRequest.getTitle())
                .url(newPullRequest.getUrl())
                .telegramId(newPullRequest.getAuthor().getTelegramId())
                .reviewerChanges(reviewerChanges)
                .build();
    }
}
