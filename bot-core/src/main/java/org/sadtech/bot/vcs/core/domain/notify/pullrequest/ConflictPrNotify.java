package org.sadtech.bot.vcs.core.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.vcs.core.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;

@Getter
public class ConflictPrNotify extends PrNotify {

    @Builder
    private ConflictPrNotify(
            Set<String> logins,
            String name,
            String url
    ) {
        super(logins, name, url);
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
