package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import lombok.Builder;
import lombok.Getter;

import static dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.UpdateMrNotifyFields.CLASS_NAME;

@Getter
@FieldNames
public class UpdateMrNotify extends MrNotify {

    public static final String TYPE = CLASS_NAME;

    private final String author;
    private final Long allTasks;
    private final Long allResolvedTasks;
    private final Long personTasks;
    private final Long personResolvedTasks;
    private final String comment;

    @Builder
    private UpdateMrNotify(
            Long mrId,
            String name,
            String url,
            String author,
            String projectName,
            Long allTasks,
            Long allResolvedTasks,
            Long personTasks,
            Long personResolvedTasks,
            String comment
    ) {
        super(mrId, projectName, name, url);
        this.author = author;
        this.allTasks = allTasks;
        this.allResolvedTasks = allResolvedTasks;
        this.personTasks = personTasks;
        this.personResolvedTasks = personResolvedTasks;
        this.comment = comment;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
