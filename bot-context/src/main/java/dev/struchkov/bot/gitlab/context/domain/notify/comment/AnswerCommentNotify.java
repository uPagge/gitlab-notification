package dev.struchkov.bot.gitlab.context.domain.notify.comment;

import dev.struchkov.bot.gitlab.context.domain.Answer;
import dev.struchkov.bot.gitlab.context.utils.Smile;
import lombok.Builder;
import lombok.Getter;
import dev.struchkov.bot.gitlab.context.domain.notify.Notify;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class AnswerCommentNotify extends Notify {

    private final String youMessage;
    private final String url;
    private final List<Answer> answers;

    @Builder
    protected AnswerCommentNotify(
            String youMessage,
            String url,
            List<Answer> answers
    ) {
        this.youMessage = youMessage;
        this.url = url;
        this.answers = answers;
    }

    @Override
    public String generateMessage(AppSettingService settingService) {
        final String answerText = answers.stream()
                .map(answer -> answer.getAuthorName() + ": " + answer.getMessage().substring(0, Math.min(answer.getMessage().length(), 500)))
                .collect(Collectors.joining("\n\n"));
        return MessageFormat.format(
                "{0} *Новые ответы* на [комментарий]({1}){2}" +
                        "{3}{2}" +
                        "{4}",
                Smile.COMMENT,
                url,
                Smile.HR,
                escapeMarkdown(youMessage.substring(0, Math.min(youMessage.length(), 180))),
                escapeMarkdown(answerText)
        );
    }

}
