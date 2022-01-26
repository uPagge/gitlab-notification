package dev.struchkov.bot.gitlab.context.domain.notify.pullrequest;

import lombok.Getter;
import dev.struchkov.bot.gitlab.context.domain.notify.Notify;

@Getter
public abstract class PrNotify extends Notify {

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
