package dev.struchkov.bot.gitlab.context.domain.notify.issue;

import dev.struchkov.bot.gitlab.context.domain.IssueState;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Dmitry Sheyko 23.01.2021
 */
@Getter
public class StatusIssueNotify extends IssueNotify{

    public static final String TYPE = "StatusIssueNotify";

    private final IssueState oldStatus;
    private final IssueState newStatus;

    @Builder
    private StatusIssueNotify(
            String name,
            String url,
            String projectName,
            String issueType,
            IssueState oldStatus,
            IssueState newStatus
    ) {
        super(projectName, name, url, issueType);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}