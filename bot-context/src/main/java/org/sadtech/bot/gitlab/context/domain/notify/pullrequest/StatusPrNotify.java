package org.sadtech.bot.gitlab.context.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.utils.Smile;
import org.sadtech.bot.vsc.context.domain.PullRequestStatus;

import java.text.MessageFormat;
import java.util.Set;

@Getter
public class StatusPrNotify extends PrNotify {

    private final PullRequestStatus oldStatus;
    private final PullRequestStatus newStatus;

    @Builder
    private StatusPrNotify(
            Set<String> recipients,
            String name,
            String url,
            String projectKey,
            String repositorySlug,
            PullRequestStatus oldStatus,
            PullRequestStatus newStatus
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
