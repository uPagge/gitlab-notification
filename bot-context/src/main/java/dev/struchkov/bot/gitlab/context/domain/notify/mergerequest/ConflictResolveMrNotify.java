package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ConflictResolveMrNotify extends MrNotify {

    public static final String TYPE = "ConflictResolveMrNotify";

    private final String sourceBranch;

    @Builder
    private ConflictResolveMrNotify(
            Long mrId,
            String name,
            String url,
            String projectKey,
            String sourceBranch
    ) {
        super(mrId, projectKey, name, url);
        this.sourceBranch = sourceBranch;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
