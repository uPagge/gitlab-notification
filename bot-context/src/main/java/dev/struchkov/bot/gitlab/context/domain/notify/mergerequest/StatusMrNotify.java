package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import lombok.Builder;
import lombok.Getter;

import static dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.StatusMrNotifyFields.CLASS_NAME;

@Getter
@FieldNames
public class StatusMrNotify extends MrNotify {

    public static final String TYPE = CLASS_NAME;

    private final MergeRequestState oldStatus;
    private final MergeRequestState newStatus;

    @Builder
    private StatusMrNotify(
            Long mrId,
            String name,
            String url,
            String projectName,
            MergeRequestState oldStatus,
            MergeRequestState newStatus
    ) {
        super(mrId, projectName, name, url);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
