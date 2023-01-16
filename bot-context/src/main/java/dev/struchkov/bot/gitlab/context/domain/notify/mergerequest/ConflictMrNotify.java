package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ConflictMrNotify extends MrNotify {

    public static final String TYPE = "ConflictPrNotify";

    private final String sourceBranch;

    @Builder
    private ConflictMrNotify(
            String name,
            String url,
            String projectKey,
            String sourceBranch
    ) {
        super(projectKey, name, url);
        this.sourceBranch = sourceBranch;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
