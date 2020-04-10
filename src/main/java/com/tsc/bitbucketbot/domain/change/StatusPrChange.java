package com.tsc.bitbucketbot.domain.change;

import com.tsc.bitbucketbot.domain.PullRequestStatus;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class StatusPrChange extends PrChange {

    private final PullRequestStatus oldStatus;
    private final PullRequestStatus newStatus;

    @Builder
    private StatusPrChange(
            Set<Long> telegramIds,
            String name,
            String url,
            PullRequestStatus oldStatus,
            PullRequestStatus newStatus) {
        super(ChangeType.STATUS_PR, telegramIds, name, url);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

}
