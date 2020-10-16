package org.sadtech.bot.vcs.core.domain.notify.pullrequest;

import lombok.Getter;
import org.sadtech.bot.vcs.core.domain.EntityType;
import org.sadtech.bot.vcs.core.domain.notify.Notify;

import java.util.Set;

@Getter
public abstract class PrNotify extends Notify {

    protected final String projectKey;
    protected final String repositorySlug;
    protected final String title;
    protected final String url;

    protected PrNotify(
            Set<String> recipients,
            String projectKey,
            String repositorySlug,
            String title,
            String url
    ) {
        super(EntityType.PERSON, recipients);
        this.projectKey = projectKey;
        this.repositorySlug = repositorySlug;
        this.title = title;
        this.url = url;
    }

}
