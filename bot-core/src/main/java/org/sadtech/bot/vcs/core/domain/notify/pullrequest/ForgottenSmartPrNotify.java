package org.sadtech.bot.vcs.core.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.vcs.core.utils.Smile;

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
    protected ForgottenSmartPrNotify(Set<String> recipients, String title, String url) {
        super(recipients, title, url);
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Напоминание о просмотре PullRequest*" +
                        "{3}[{1}]({2})",
                Smile.SMART, title, url, Smile.HR
        );
    }

}
