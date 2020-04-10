package com.tsc.bitbucketbot.domain.change;

import com.tsc.bitbucketbot.domain.util.ReviewerChange;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ReviewersPrChange extends PrChange {

    private final List<ReviewerChange> reviewerChanges;

    @Builder
    private ReviewersPrChange(
            Set<Long> telegramIds,
            String name,
            String url,
            List<ReviewerChange> reviewerChanges) {
        super(ChangeType.REVIEWERS, telegramIds, name, url);
        this.reviewerChanges = reviewerChanges;
    }

}
