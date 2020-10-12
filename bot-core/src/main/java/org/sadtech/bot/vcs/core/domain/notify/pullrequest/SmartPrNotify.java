package org.sadtech.bot.vcs.core.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.vcs.core.domain.entity.Reviewer;
import org.sadtech.bot.vcs.core.utils.Smile;

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
    protected SmartPrNotify(Set<String> recipients, String title, String url, Reviewer reviewerTriggered) {
        super(recipients, title, url);
        this.reviewerTriggered = reviewerTriggered;
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Напоминание о просмотре PullRequest*" +
                        "{3}[{1}]({2})" +
                        "{3}" +
                        "{4} изменил свое решение на {5}\n\n",
                Smile.SMART, title, url, Smile.HR, reviewerTriggered.getPersonLogin(), reviewerTriggered.getStatus().getValue()
        );
    }

}
