package org.sadtech.bot.vcs.core.domain.change.pullrequest;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.vcs.core.domain.change.Change;
import org.sadtech.bot.vcs.core.domain.change.ChangeType;

import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class PrChange extends Change {

    protected final String title;
    protected final String url;

    protected PrChange(ChangeType type, Set<Long> telegramIds, String title, String url) {
        super(type, telegramIds);
        this.title = title;
        this.url = url;
    }

}
