package org.sadtech.bot.gitlab.context.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.utils.Smile;

import java.text.MessageFormat;

@Getter
public class ConflictPrNotify extends PrNotify {

    @Builder
    private ConflictPrNotify(
            String name,
            String url,
            String projectKey
    ) {
        super(projectKey, name, url);
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Внимание конфликт в ПР | {4}*" +
                        "{1}" +
                        "[{2}]({3})\n\n",
                Smile.DANGEROUS, Smile.HR, title, url, projectName
        );
    }

}
