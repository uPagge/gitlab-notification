package org.sadtech.bot.bitbucketbot.domain.change.pullrequest;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.bitbucketbot.domain.change.ChangeType;
import org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange;

import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ReviewersPrChange extends PrChange {

    private final List<ReviewerChange> reviewerChanges;

    @Builder
    private ReviewersPrChange(
            Set<Long> telegramIds,
            String title,
            String url,
            List<ReviewerChange> reviewerChanges) {
        super(ChangeType.REVIEWERS, telegramIds, title, url);
        this.reviewerChanges = reviewerChanges;
    }

}
