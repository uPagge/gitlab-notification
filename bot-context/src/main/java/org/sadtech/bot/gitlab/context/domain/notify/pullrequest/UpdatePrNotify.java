package org.sadtech.bot.gitlab.context.domain.notify.pullrequest;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.gitlab.context.utils.Smile;

import java.text.MessageFormat;

@Getter
public class UpdatePrNotify extends PrNotify {

    private final String author;

    @Builder
    private UpdatePrNotify(
            String name,
            String url,
            String author,
            String projectKey
    ) {
        super(projectKey, name, url);
        this.author = author;
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Обновление PullRequest | {6}*{3}" +
                        "[{1}]({2})" +
                        "{3}" +
                        "{4}: {5}\n\n",
                Smile.UPDATE, title, url, Smile.HR, Smile.AUTHOR, author, projectName
        );
    }

}
