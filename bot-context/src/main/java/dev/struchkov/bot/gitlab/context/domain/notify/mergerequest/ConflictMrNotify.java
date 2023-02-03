package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ConflictMrNotify extends MrNotify {

    public static final String TYPE = "ConflictPrNotify";

    private final String sourceBranch;

    @Builder
    private ConflictMrNotify(
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