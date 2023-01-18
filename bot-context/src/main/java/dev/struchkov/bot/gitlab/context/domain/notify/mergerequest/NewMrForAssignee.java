package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Set;

@Getter
public class NewMrForAssignee extends NewMrNotify {

    public static final String TYPE = "NewMrForAssignee";

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
