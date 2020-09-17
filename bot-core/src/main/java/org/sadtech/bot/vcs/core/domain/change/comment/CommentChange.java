package org.sadtech.bot.vcs.core.domain.change.comment;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.vcs.core.domain.change.Change;
import org.sadtech.bot.vcs.core.domain.change.ChangeType;
import org.sadtech.bot.vcs.core.utils.Smile;

import java.text.MessageFormat;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CommentChange extends Change {

    private final String authorName;
    private final String message;
    private final String url;

    @Builder
    private CommentChange(
            Set<Long> telegramIds,
            String url,
            String authorName,
            String message
    ) {
        super(ChangeType.NEW_COMMENT, telegramIds);
        this.authorName = authorName;
        this.message = message;
        this.url = url;
    }

    @Override
    public String generateMessage() {
        return MessageFormat.format(
                "{0} *Новое упоминание* | [ПР]({1}){2}" +
                        "{3}: {4}",
                Smile.BELL, url, Smile.HR, authorName, message.replaceAll("@[\\w]+", "")
        );
    }
}


