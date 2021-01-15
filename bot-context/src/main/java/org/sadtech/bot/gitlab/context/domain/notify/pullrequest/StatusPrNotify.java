package org.sadtech.bot.gitlab.context.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.utils.Smile;

import java.text.MessageFormat;

@Getter
public class StatusPrNotify extends PrNotify {

    private final MergeRequestState oldStatus;
    private final MergeRequestState newStatus;

    @Builder
    private StatusPrNotify(
            String name,
            String url,
            String projectName,
            MergeRequestState oldStatus,
            MergeRequestState newStatus
    ) {
        super(projectName, name, url);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Изменился статус PullRequest | {7}*{1}" +
                        "[{2}]({3}){1}" +
                        "{4} {5} {6}\n\n",
                Smile.PEN, Smile.HR, title, url, oldStatus.name(), Smile.ARROW, newStatus.name(), projectName
        );
    }

}
