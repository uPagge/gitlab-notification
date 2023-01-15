package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StatusPrNotify extends PrNotify {

    public static final String TYPE = "StatusPrNotify";

    private final MergeRequestState oldStatus;
    private final MergeRequestState newStatus;

    @Builder
    private StatusPrNotify(
            String name,
            String url,
            String projectName,
            MergeRequestState oldStatus,
            MergeRequestState newStatus
    ) {
        super(projectName, name, url);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
