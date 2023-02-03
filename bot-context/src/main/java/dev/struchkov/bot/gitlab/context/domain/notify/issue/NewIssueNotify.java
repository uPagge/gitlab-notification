package dev.struchkov.bot.gitlab.context.domain.notify.issue;

import lombok.Builder;
import lombok.Getter;

import java.util.Set;

/**
 * @author Dmitry Sheyko 23.01.2021
 */
@Getter
public class NewIssueNotify extends IssueNotify {

    public static final String TYPE = "NewIssueNotify";

    private final String author;
    private final String description;
    private final String dueDate;
    private final Set<String> labels;
    private final String confidential;

    @Builder
    public NewIssueNotify(
            String projectName,
            String title,
            String url,
            String issueType,
            String author,
            String description,
            String dueDate,
            Set<String> labels,
            String confidential
    ) {
        super(projectName, title, url, issueType);
        this.author = author;
        this.description = description;
        this.dueDate = dueDate;
        this.labels = labels;
        this.confidential = confidential;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}