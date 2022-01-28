package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import dev.struchkov.haiti.context.service.SimpleManagerService;
import lombok.NonNull;

import java.util.List;

/**
 * @author upagge 11.02.2021
 */
public interface DiscussionService extends SimpleManagerService<Discussion, String> {

    /**
     * Метод отправляющий коментарий в дискуссию.
     *
     * @param discussionId Идентификатор дискуссии
     * @param text         Текст комментария
     */
    void answer(@NonNull String discussionId, @NonNull String text);

    /**
     * Получить все дискусси для MR.
     */
    List<Discussion> getAllByMergeRequestId(@NonNull Long mergeRequestId);

}
