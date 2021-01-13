package org.sadtech.bot.vsc.bitbucketbot.context.domain.notify.comment;

import lombok.Builder;
import lombok.Getter;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.EntityType;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.notify.Notify;
import org.sadtech.bot.vsc.bitbucketbot.context.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;

@Getter
public class CommentNotify extends Notify {

    private final String authorName;
    private final String message;
    private final String url;

    @Builder
    private CommentNotify(
            Set<String> recipients,
            String url,
            String authorName,
            String message
    ) {
        super(EntityType.PERSON, recipients);
        this.authorName = authorName;
        this.message = message;
        this.url = url;
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Новое упоминание* | [ПР]({1}){2}" +
                        "*{3}*: {4}",
                Smile.COMMENT, url, Smile.HR, authorName, escapeMarkdown(message)
        );
    }

}


