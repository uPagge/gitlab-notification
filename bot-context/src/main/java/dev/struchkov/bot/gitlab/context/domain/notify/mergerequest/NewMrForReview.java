package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import dev.struchkov.haiti.utils.fieldconstants.annotation.FieldNames;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

import static dev.struchkov.bot.gitlab.context.domain.notify.mergerequest.NewMrForReviewFields.CLASS_NAME;

@Getter
@FieldNames
public class NewMrForReview extends NewMrNotify {

    public static final String TYPE = CLASS_NAME;

    private final String assignee;

    @Builder
    private NewMrForReview(
            Long mrId,
            String title,
            String url,
            String description,
            String author,
            String projectName,
            String targetBranch,
            String sourceBranch,
            Set<String> labels,
            String assignee
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
        this.assignee = assignee;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
