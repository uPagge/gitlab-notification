package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
public class NewPrNotify extends PrNotify {

    public static final String TYPE = "NewPrNotify";

    private final String description;
    private final String author;
    private final String targetBranch;
    private final String sourceBranch;
    private final Set<String> labels;

    @Builder
    private NewPrNotify(
            String title,
            String url,
            String description,
            String author,
            String projectName,
            String targetBranch,
            String sourceBranch,
            Set<String> labels
    ) {
        super(projectName, title, url);
        this.description = description;
        this.author = author;
        this.targetBranch = targetBranch;
        this.sourceBranch = sourceBranch;
        this.labels = labels;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
