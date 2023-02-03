package dev.struchkov.bot.gitlab.context.domain.notify.issue;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Dmitry Sheyko 25.01.2021
 */
@Getter
public class DueDateIssueNotify extends IssueNotify {

    public static final String TYPE = "DueDateIssueNotify";

    private final String oldDueDate;
    private final String newDueDate;

    @Builder
    public DueDateIssueNotify(
            String projectName,
            String title,
            String url,
            String issueType,
            String oldDueDate,
            String newDueDate
    ) {
        super(projectName, title, url, issueType);
        this.oldDueDate = oldDueDate;
        this.newDueDate = newDueDate;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}