package org.sadtech.bot.gitlab.context.service;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Discussion;
import org.sadtech.haiti.context.service.SimpleManagerService;

/**
 * // TODO: 11.02.2021 Добавить описание.
 *
 * @author upagge 11.02.2021
 */
public interface DiscussionService extends SimpleManagerService<Discussion, String> {

    void answer(@NonNull String discussionId, @NonNull String text);

}
