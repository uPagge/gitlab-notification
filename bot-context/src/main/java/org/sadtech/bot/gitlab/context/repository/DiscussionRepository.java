package org.sadtech.bot.gitlab.context.repository;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Discussion;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;

import java.util.List;

/**
 * // TODO: 11.02.2021 Добавить описание.
 *
 * @author upagge 11.02.2021
 */
public interface DiscussionRepository extends SimpleManagerRepository<Discussion, String> {

    List<Discussion> findAllByMergeRequestId(@NonNull Long mergeRequestId);

}
