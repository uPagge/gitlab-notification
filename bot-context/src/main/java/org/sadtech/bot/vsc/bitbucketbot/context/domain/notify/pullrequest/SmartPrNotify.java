package org.sadtech.bot.vsc.bitbucketbot.context.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Reviewer;
import org.sadtech.bot.vsc.bitbucketbot.context.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;

/**
 * // TODO: 11.10.2020 Добавить описание.
 *
 * @author upagge 11.10.2020
 */
@Getter
public class SmartPrNotify extends PrNotify {

    private final Reviewer reviewerTriggered;

    @Builder
    protected SmartPrNotify(
            Set<String> recipients,
            String title,
            String url,
            String projectKey,
            String repositorySlug,
            Reviewer reviewerTriggered
    ) {
        super(recipients, projectKey, repositorySlug, title, url);
        this.reviewerTriggered = reviewerTriggered;
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Напоминание о PullRequest | {6} | {7}*" +
                        "{3}[{1}]({2})" +
                        "{3}" +
                        "{4} изменил свое решение на {5}\n\n",
                Smile.SMART, title, url, Smile.HR, reviewerTriggered.getPersonLogin(), reviewerTriggered.getStatus().getValue(),
                projectKey, repositorySlug
        );
    }

}
