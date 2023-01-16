package dev.struchkov.bot.gitlab.context.domain.notify.mergerequest;

import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import lombok.Getter;

@Getter
public abstract class MrNotify implements Notify {

    protected final String projectName;
    protected final String title;
    protected final String url;

    protected MrNotify(
            String projectName,
            String title,
            String url
    ) {
        this.projectName = projectName;
        this.title = title;
        this.url = url;
    }

}
