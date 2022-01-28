package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import dev.struchkov.haiti.context.repository.SimpleManagerRepository;
import lombok.NonNull;

import java.util.List;

/**
 * @author upagge 11.02.2021
 */
public interface DiscussionRepository extends SimpleManagerRepository<Discussion, String> {

    /**
     * Вернуть все дискусии для MR
     */
    List<Discussion> findAllByMergeRequestId(@NonNull Long mergeRequestId);

}
