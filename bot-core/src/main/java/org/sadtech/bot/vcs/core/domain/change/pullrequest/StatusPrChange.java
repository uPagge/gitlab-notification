package org.sadtech.bot.vcs.core.domain.change.pullrequest;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.vcs.core.domain.PullRequestStatus;
import org.sadtech.bot.vcs.core.domain.change.ChangeType;
import org.sadtech.bot.vcs.core.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class StatusPrChange extends PrChange {

    private final PullRequestStatus oldStatus;
    private final PullRequestStatus newStatus;

    @Builder
    private StatusPrChange(
            Set<Long> telegramIds,
            String name,
            String url,
            PullRequestStatus oldStatus,
            PullRequestStatus newStatus) {
        super(ChangeType.STATUS_PR, telegramIds, name, url);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Изменился статус вашего ПР*{1}" +
                        "[{2}]({3}){4}" +
                        "{5} -> {6}\n\n",
                Smile.PEN, Smile.HR, title, url, Smile.HR, oldStatus.name(), newStatus.name()
        );
    }

}
