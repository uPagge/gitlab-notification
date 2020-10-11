package org.sadtech.bot.vcs.core.domain.notify.pullrequest;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.vcs.core.domain.PullRequestStatus;
import org.sadtech.bot.vcs.core.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class StatusPrNotify extends PrNotify {

    private final PullRequestStatus oldStatus;
    private final PullRequestStatus newStatus;

    @Builder
    private StatusPrNotify(
            Set<String> recipients,
            String name,
            String url,
            PullRequestStatus oldStatus,
            PullRequestStatus newStatus) {
        super(recipients, name, url);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Изменился статус вашего ПР*{1}" +
                        "[{2}]({3}){1}" +
                        "{4} -> {5}\n\n",
                Smile.PEN, Smile.HR, title, url, oldStatus.name(), newStatus.name()
        );
    }

}
