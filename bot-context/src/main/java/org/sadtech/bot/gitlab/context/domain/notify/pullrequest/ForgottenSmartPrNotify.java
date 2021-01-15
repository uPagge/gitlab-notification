package org.sadtech.bot.gitlab.context.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.utils.Smile;

import java.text.MessageFormat;

/**
 * // TODO: 11.10.2020 Добавить описание.
 *
 * @author upagge 11.10.2020
 */
@Getter
public class ForgottenSmartPrNotify extends PrNotify {

    @Builder
    protected ForgottenSmartPrNotify(
            String title,
            String url,
            String projectName,
            String repositorySlug
    ) {
        super(projectName, title, url);
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Напоминание о просмотре PullRequest  | {4}*" +
                        "{3}[{1}]({2})",
                Smile.SMART, title, url, Smile.HR, projectName
        );
    }

}
