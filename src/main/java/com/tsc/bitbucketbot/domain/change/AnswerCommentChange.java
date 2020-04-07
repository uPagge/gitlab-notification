package com.tsc.bitbucketbot.domain.change;

import com.tsc.bitbucketbot.domain.Answer;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Singular;

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
            @Singular("telegramId") Set<Long> telegramId,
            String youMessage,
            String url,
            List<Answer> answers
    ) {
        super(ChangeType.NEW_ANSWERS_COMMENT, telegramId);
        this.youMessage = youMessage;
        this.url = url;
        this.answers = answers;
    }

}
