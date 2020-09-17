package org.sadtech.bot.vcs.bitbucket.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sadtech.bot.vcs.bitbucket.sdk.domain.CommentState;
import org.sadtech.bot.vcs.core.domain.TaskStatus;
import org.sadtech.bot.vcs.core.exception.NotFoundException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Converter {

    public static TaskStatus taskStatus(CommentState commentState) {
        switch (commentState) {
            case OPEN:
                return TaskStatus.OPEN;
            case RESOLVED:
                return TaskStatus.RESOLVED;
            default:
                throw new NotFoundException("Неизвестный статус задачи");
        }
    }

}
