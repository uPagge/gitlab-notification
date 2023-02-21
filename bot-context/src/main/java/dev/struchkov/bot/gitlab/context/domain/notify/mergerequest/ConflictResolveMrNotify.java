package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import lombok.Builder;
import lombok.Getter;

import static dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.ConflictResolveMrNotifyFields.CLASS_NAME;

@Getter
@FieldNames
public class ConflictResolveMrNotify extends MrNotify {

    public static final String TYPE = CLASS_NAME;

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
