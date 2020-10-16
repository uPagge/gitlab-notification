package org.sadtech.bot.vcs.core.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.vcs.core.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;

@Getter
public class UpdatePrNotify extends PrNotify {

    private final String author;

    @Builder
    private UpdatePrNotify(
            Set<String> recipients,
            String name,
            String url,
            String author,
            String projectKey,
            String repositorySlug
    ) {
        super(recipients, projectKey, repositorySlug, name, url);
        this.author = author;
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Обновление PullRequest | {6} | {7}*{3}" +
                        "[{1}]({2})" +
                        "{3}" +
                        "{4}: {5}\n\n",
                Smile.UPDATE, title, url, Smile.HR, Smile.AUTHOR, author, projectKey, repositorySlug
        );
    }

}
