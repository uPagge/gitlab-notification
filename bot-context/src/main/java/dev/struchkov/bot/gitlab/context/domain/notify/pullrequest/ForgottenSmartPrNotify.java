package dev.struchkov.bot.gitlab.context.domain.notify.pullrequest;

import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;
import lombok.Getter;

import java.text.MessageFormat;

/**
 * // TODO: 11.10.2020 Добавить описание.
 *
 * @author upagge 11.10.2020
 */
@Getter
//TODO [28.01.2022]: Решить доработать и оставить или удалить
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
                "{0} *MergeRequest Review Reminder  | {4}*{3}[{1}]({2})",
                Smile.SMART.getValue(), title, url, Smile.HR.getValue(), projectName
        );
    }

}
