package org.sadtech.bot.gitlab.context.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.utils.Smile;

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
            String author,
            String projectKey,
            String repositorySlug
    ) {
        super(recipients, projectKey, repositorySlug, title, url);
        this.description = description;
        this.author = author;
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Новый PullRequest | {7} | {8}*{1}" +
                        "[{2}]({3})" +
                        "{1}{4}{5}: {6}\n\n",
                Smile.FUN, Smile.HR, title, url,
                (description != null && !"".equals(description)) ? escapeMarkdown(description) + Smile.HR : "",
                Smile.AUTHOR, author, projectKey, repositorySlug
        );
    }

}
