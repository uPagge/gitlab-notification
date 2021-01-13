package org.sadtech.bot.gitlab.context.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;

/**
 * // TODO: 11.10.2020 Добавить описание.
 *
 * @author upagge 11.10.2020
 */
@Getter
public class ForgottenSmartPrNotify extends PrNotify {

    @Builder
    protected ForgottenSmartPrNotify(
            Set<String> recipients,
            String title,
            String url,
            String projectKey,
            String repositorySlug
    ) {
        super(recipients, projectKey, repositorySlug, title, url);
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Напоминание о просмотре PullRequest  | {4} | {5}*" +
                        "{3}[{1}]({2})",
                Smile.SMART, title, url, Smile.HR, projectKey, repositorySlug
        );
    }

}
