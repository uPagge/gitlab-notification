package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Set;

import static dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.NewMrForAssigneeFields.CLASS_NAME;

@Getter
@FieldNames
public class NewMrForAssignee extends NewMrNotify {

    public static final String TYPE = CLASS_NAME;

    private final List<String> reviewers;
    private final String oldAssigneeName;
    private final String newAssigneeName;

    @Builder
    private NewMrForAssignee(
            Long mrId,
            String title,
            String url,
            String description,
            String author,
            String projectName,
            String targetBranch,
            String sourceBranch,
            Set<String> labels,
            @Singular List<String> reviewers,
            String oldAssigneeName,
            String newAssigneeName
    ) {
        super(
                mrId,
                title,
                url,
                description,
                author,
                projectName,
                targetBranch,
                sourceBranch,
                labels
        );
        this.reviewers = reviewers;
        this.oldAssigneeName = oldAssigneeName;
        this.newAssigneeName = newAssigneeName;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
