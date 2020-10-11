package org.sadtech.bot.vcs.core.domain.notify.comment;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.vcs.core.domain.Answer;
import org.sadtech.bot.vcs.core.domain.EntityType;
import org.sadtech.bot.vcs.core.domain.notify.Notify;
import org.sadtech.bot.vcs.core.utils.Smile;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode(callSuper = true)
public class AnswerCommentNotify extends Notify {

    private final String youMessage;
    private final String url;
    private final List<Answer> answers;

    @Builder
    protected AnswerCommentNotify(
            Set<String> recipients,
            String youMessage,
            String url,
            List<Answer> answers
    ) {
        super(EntityType.PERSON, recipients);
        this.youMessage = youMessage;
        this.url = url;
        this.answers = answers;
    }

    @Override
    public String generateMessage() {
        final String answerText = answers.stream()
                .map(answer -> "*" + answer.getAuthorName() + "*: " + answer.getMessage().substring(0, Math.min(answer.getMessage().length(), 500)))
                .collect(Collectors.joining("\n\n"));
        return MessageFormat.format(
                "{0} *Новые ответы на ваш комментарий* | [ПР]({1}){2}" +
                        "{3}{2}" +
                        "{4}",
                Smile.BELL,
                url,
                Smile.HR,
                escapeMarkdown(youMessage.substring(0, Math.min(youMessage.length(), 180))),
                escapeMarkdown(answerText)
        );
    }

}
