package org.sadtech.bot.vcs.core.domain.notify.pullrequest;

import lombok.Getter;
import org.sadtech.bot.vcs.core.domain.EntityType;
import org.sadtech.bot.vcs.core.domain.notify.Notify;

import java.util.Set;

@Getter
public abstract class PrNotify extends Notify {

    protected final String title;
    protected final String url;

    protected PrNotify(Set<String> recipients, String title, String url) {
        super(EntityType.PERSON, recipients);
        this.title = title;
        this.url = url;
    }

}
