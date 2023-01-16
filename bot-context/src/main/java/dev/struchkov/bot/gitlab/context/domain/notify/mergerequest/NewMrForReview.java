package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
public class NewMrForReview extends NewMrNotify {

    public static final String TYPE = "NewMrForReview";

    @Builder
    private NewMrForReview(
            String title,
            String url,
            String description,
            String author,
            String projectName,
            String targetBranch,
            String sourceBranch,
            Set<String> labels
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
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
