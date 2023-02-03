package dev.struchkov.bot.gitlab.context.domain.notify.issue;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Dmitry Sheyko 25.01.2021
 */
@Getter
public class DescriptionIssueNotify extends IssueNotify {

    public static final String TYPE = "DescriptionIssueNotify";

    private final String newDescription;

    @Builder
    public DescriptionIssueNotify(
            String projectName,
            String title,
            String url,
            String issueType,
            String newDescription
    ) {
        super(projectName, title, url, issueType);
        this.newDescription = newDescription;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}