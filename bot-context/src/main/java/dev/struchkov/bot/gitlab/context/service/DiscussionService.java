package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import dev.struchkov.haiti.context.service.SimpleManagerService;
import lombok.NonNull;

import java.util.List;

/**
 * // TODO: 11.02.2021 Добавить описание.
 *
 * @author upagge 11.02.2021
 */
public interface DiscussionService extends SimpleManagerService<Discussion, String> {

    void answer(@NonNull String discussionId, @NonNull String text);

    List<Discussion> getAllByMergeRequestId(@NonNull Long mergeRequestId);

}
