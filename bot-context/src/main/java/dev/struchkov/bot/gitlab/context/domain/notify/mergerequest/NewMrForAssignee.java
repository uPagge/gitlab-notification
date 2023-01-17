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

    @Builder
    private NewMrForAssignee(
            String title,
            String url,
            String description,
            String author,
            String projectName,
            String targetBranch,
            String sourceBranch,
            Set<String> labels,
            @Singular List<String> reviewers
    ) {
        super(
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
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
