package org.sadtech.bot.gitlab.context.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;

@Getter
public class ConflictPrNotify extends PrNotify {

    @Builder
    private ConflictPrNotify(
            Set<String> recipients,
            String name,
            String url,
            String projectKey,
            String repositorySlug
    ) {
        super(recipients, projectKey, repositorySlug, name, url);
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Внимание конфликт в ПР | {4} | {5}*" +
                        "{1}" +
                        "[{2}]({3})\n\n",
                Smile.DANGEROUS, Smile.HR, title, url, projectKey, repositorySlug
        );
    }

}
