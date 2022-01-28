package dev.struchkov.bot.gitlab.context.domain.notify.pullrequest;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import lombok.Getter;

@Getter
public abstract class PrNotify implements Notify {

    protected final String projectName;
    protected final String title;
    protected final String url;

    protected PrNotify(
            String projectName,
            String title,
            String url
    ) {
        this.projectName = projectName;
        this.title = title;
        this.url = url;
    }

}
