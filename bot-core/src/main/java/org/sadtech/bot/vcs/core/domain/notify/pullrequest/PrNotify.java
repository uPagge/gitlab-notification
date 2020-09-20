package org.sadtech.bot.vcs.core.domain.notify.pullrequest;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.vcs.core.domain.notify.Notify;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class PrNotify extends Notify {

    protected final String title;
    protected final String url;

    protected PrNotify(Set<String> logins, String title, String url) {
        super(logins);
        this.title = title;
        this.url = url;
    }

}
