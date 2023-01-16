package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import lombok.NonNull;

import java.util.List;
import java.util.Set;

/**
 * @author upagge 11.02.2021
 */
public interface DiscussionService {

    Discussion create(@NonNull Discussion discussion);

    Discussion update(@NonNull Discussion discussion);

    List<Discussion> updateAll(@NonNull List<Discussion> discussions);

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

    ExistContainer<Discussion, String> existsById(@NonNull Set<String> discussionIds);

    List<Discussion> createAll(@NonNull List<Discussion> newDiscussions);

    List<Discussion> getAll();

    Set<String> getAllIds();

    void deleteById(@NonNull String discussionId);

    void cleanOld();

}
