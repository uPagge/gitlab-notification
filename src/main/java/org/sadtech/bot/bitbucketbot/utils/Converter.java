package org.sadtech.bot.bitbucketbot.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sadtech.bot.bitbucketbot.domain.TaskStatus;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.CommentState;
import org.sadtech.bot.bitbucketbot.exception.NotFoundException;

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
