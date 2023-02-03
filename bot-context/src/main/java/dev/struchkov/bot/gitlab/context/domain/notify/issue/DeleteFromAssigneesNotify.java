package dev.struchkov.bot.gitlab.context.domain.notify.issue;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Dmitry Sheyko 25.01.2021
 */
@Getter
public class DeleteFromAssigneesNotify extends IssueNotify {

    public static final String TYPE = "DeleteFromAssigneesOfIssueNotify";

    private final String updateDate;

    @Builder
    public DeleteFromAssigneesNotify(
            String projectName,
            String title,
            String url,
            String issueType,
            String updateDate
    ) {
        super(projectName, title, url, issueType);
        this.updateDate = updateDate;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}
