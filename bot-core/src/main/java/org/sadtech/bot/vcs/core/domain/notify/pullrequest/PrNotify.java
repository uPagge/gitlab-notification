package org.sadtech.bot.vcs.core.domain.notify.pullrequest;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.vcs.core.domain.notify.Notify;
import org.sadtech.bot.vcs.core.domain.notify.NotifyType;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class PrNotify extends Notify {

    protected final String title;
    protected final String url;

    protected PrNotify(NotifyType type, Set<Long> telegramIds, String title, String url) {
        super(type, telegramIds);
        this.title = title;
        this.url = url;
    }

}
