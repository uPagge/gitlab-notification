package org.sadtech.bot.bitbucketbot.domain.change.pullrequest;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.bitbucketbot.domain.change.ChangeType;
import org.sadtech.bot.bitbucketbot.domain.util.ReviewerChange;

import java.util.Collections;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ReviewersPrChange extends PrChange {

    private final List<ReviewerChange> reviewerChanges;

    @Builder
    private ReviewersPrChange(
            Long telegramId,
            String title,
            String url,
            List<ReviewerChange> reviewerChanges) {
        super(ChangeType.REVIEWERS, Collections.singleton(telegramId), title, url);
        this.reviewerChanges = reviewerChanges;
    }

}
