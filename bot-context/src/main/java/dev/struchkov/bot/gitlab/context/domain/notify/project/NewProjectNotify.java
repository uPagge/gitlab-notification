package dev.struchkov.bot.gitlab.context.domain.notify.project;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import lombok.Builder;
import lombok.Getter;

import static dev.struchkov.bot.gitlab.context.domain.notify.project.NewProjectNotifyFields.CLASS_NAME;

/**
 * @author upagge 15.01.2021
 */
@Getter
@FieldNames
public final class NewProjectNotify implements Notify {

    public static final String TYPE = CLASS_NAME;

    private final Long projectId;
    private final String projectName;
    private final String projectUrl;
    private final String projectDescription;
    private final String authorName;
    private final String sshUrlToRepo;
    private final String httpUrlToRepo;

    @Builder
    public NewProjectNotify(
            Long projectId,
            String projectName,
            String projectUrl,
            String projectDescription,
            String authorName,
            String sshUrlToRepo,
            String httpUrlToRepo
    ) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.projectUrl = projectUrl;
        this.projectDescription = projectDescription;
        this.authorName = authorName;
        this.sshUrlToRepo = sshUrlToRepo;
        this.httpUrlToRepo = httpUrlToRepo;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
