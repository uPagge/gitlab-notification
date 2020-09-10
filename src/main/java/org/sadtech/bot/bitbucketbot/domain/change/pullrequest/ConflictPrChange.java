package org.sadtech.bot.bitbucketbot.domain.change.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.bitbucketbot.domain.change.ChangeType;
import org.sadtech.bot.bitbucketbot.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;

@Getter
public class ConflictPrChange extends PrChange {

    @Builder
    private ConflictPrChange(
            Set<Long> telegramIds,
            String name,
            String url
    ) {
        super(ChangeType.CONFLICT_PR, telegramIds, name, url);
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Внимание конфликт в ПР*" +
                        "{1}" +
                        "[{2}]({3})\n\n",
                Smile.DANGEROUS, Smile.HR, title, url
        );
    }
}
