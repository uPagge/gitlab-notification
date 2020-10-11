package org.sadtech.bot.vcs.core.domain.notify.comment;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.vcs.core.domain.EntityType;
import org.sadtech.bot.vcs.core.domain.notify.Notify;
import org.sadtech.bot.vcs.core.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
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
                        "{3}: {4}",
                Smile.BELL, url, Smile.HR, authorName, escapeMarkdown(message.replaceAll("@[\\w]+", ""))
        );
    }

}


