package dev.struchkov.bot.gitlab.context.domain.notify.issue;

import dev.struchkov.bot.gitlab.context.domain.IssueType;
import lombok.Builder;
import lombok.Getter;

/**
 * @author Dmitry Sheyko 25.01.2021
 */
@Getter
public class TypeIssueNotify extends IssueNotify {

    public static final String TYPE = "TypeIssueNotify";

    private final IssueType oldType;
    private final IssueType newType;

    @Builder
    public TypeIssueNotify(
            String projectName,
            String title,
            String url,
            String issueType,
            IssueType oldType,
            IssueType newType
    ) {
        super(projectName, title, url, issueType);
        this.oldType = oldType;
        this.newType = newType;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}