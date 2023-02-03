package dev.struchkov.bot.gitlab.context.domain.notify.issue;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import lombok.Getter;

/**
 * @author Dmitry Sheyko 23.01.2021
 */
@Getter
public abstract class IssueNotify implements Notify {

    protected final String projectName;
    protected final String title;
    protected final String url;
    protected final String issueType;

    public IssueNotify(
            String projectName,
            String title,
            String url,
            String issueType
    ) {
        this.projectName = projectName;
        this.title = title;
        this.url = url;
        this.issueType = issueType;
    }

}