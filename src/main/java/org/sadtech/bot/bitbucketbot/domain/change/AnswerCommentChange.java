package org.sadtech.bot.bitbucketbot.domain.change;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.sadtech.bot.bitbucketbot.domain.Answer;

import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(callSuper = true)
public class AnswerCommentChange extends Change {

    private final String youMessage;
    private final String url;
    private final List<Answer> answers;

    @Builder
    protected AnswerCommentChange(
            Set<Long> telegramIds,
            String youMessage,
            String url,
            List<Answer> answers
    ) {
        super(ChangeType.NEW_ANSWERS_COMMENT, telegramIds);
        this.youMessage = youMessage;
        this.url = url;
        this.answers = answers;
    }

}
