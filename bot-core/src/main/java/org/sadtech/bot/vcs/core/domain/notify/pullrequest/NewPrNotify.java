package org.sadtech.bot.vcs.core.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.vcs.core.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;

@Getter
public class NewPrNotify extends PrNotify {

    private final String description;
    private final String author;

    @Builder
    private NewPrNotify(
            Set<String> recipients,
            String title,
            String url,
            String description,
            String author) {
        super(recipients, title, url);
        this.description = description;
        this.author = author;
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Новый Pull Request*{1}" +
                        "[{2}]({3})" +
                        "{1}{4}{5}: {6}\n\n",
                Smile.FUN, Smile.HR, title, url,
                (description != null && !"".equals(description)) ? escapeMarkdown(description) + Smile.HR : "",
                Smile.AUTHOR, author
        );
    }

}
