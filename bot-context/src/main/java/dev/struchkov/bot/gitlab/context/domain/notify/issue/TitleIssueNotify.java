package dev.struchkov.bot.gitlab.context.domain.notify.issue;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Dmitry Sheyko 25.01.2021
 */
@Getter
public class TitleIssueNotify extends IssueNotify {

    public static final String TYPE = "TitleIssueNotify";

    private final String newTitle;

    @Builder
    public TitleIssueNotify(
            String projectName,
            String title,
            String url,
            String issueType,
            String newTitle
    ) {
        super(projectName, title, url, issueType);
        this.newTitle = newTitle;
    }

    @Override
    public String getType() {
        return TYPE;
    }

}