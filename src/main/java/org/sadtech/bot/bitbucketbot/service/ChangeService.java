package org.sadtech.bot.bitbucketbot.service;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.change.Change;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.NewPrChange;
import org.sadtech.bot.bitbucketbot.domain.change.pullrequest.UpdatePrChange;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange;

import java.util.List;

/**
 * Сервис по работе с изменениями в битбакете.
 *
 * @author upagge
 * @see Change
 */
public interface ChangeService {

    NewPrChange create(@NonNull PullRequest newPullRequest);

    UpdatePrChange createUpdatePr(@NonNull PullRequest oldPullRequest, @NonNull PullRequest newPullRequest);

    Change createReviewersPr(String prName, String prUrl, String authorLogin, List<ReviewerChange> reviewerChanges);

    /**
     * Позволяет получить новые изменения.
     */
    List<Change> getNew();

}
