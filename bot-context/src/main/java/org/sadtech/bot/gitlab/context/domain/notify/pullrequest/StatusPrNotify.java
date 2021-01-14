package org.sadtech.bot.gitlab.context.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;

@Getter
public class StatusPrNotify extends PrNotify {

    private final MergeRequestState oldStatus;
    private final MergeRequestState newStatus;

    @Builder
    private StatusPrNotify(
            Set<String> recipients,
            String name,
            String url,
            String projectKey,
            String repositorySlug,
            MergeRequestState oldStatus,
            MergeRequestState newStatus
    ) {
        super(recipients, projectKey, repositorySlug, name, url);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Изменился статус PullRequest | {7} | {8}*{1}" +
                        "[{2}]({3}){1}" +
                        "{4} {5} {6}\n\n",
                Smile.PEN, Smile.HR, title, url, oldStatus.name(), Smile.ARROW, newStatus.name(), projectKey, repositorySlug
        );
    }

}
