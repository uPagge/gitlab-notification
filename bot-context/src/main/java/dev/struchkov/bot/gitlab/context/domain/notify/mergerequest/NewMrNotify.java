package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import lombok.Getter;

import java.util.Set;

@Getter
public abstract class NewMrNotify extends MrNotify {

    protected final String description;
    protected final String author;
    protected final String targetBranch;
    protected final String sourceBranch;
    protected final Set<String> labels;

    protected NewMrNotify(
            Long mrId,
            String title,
            String url,
            String description,
            String author,
            String projectName,
            String targetBranch,
            String sourceBranch,
            Set<String> labels
    ) {
        super(mrId, projectName, title, url);
        this.description = description;
        this.author = author;
        this.targetBranch = targetBranch;
        this.sourceBranch = sourceBranch;
        this.labels = labels;
    }

}
