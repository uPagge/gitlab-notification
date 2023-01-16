package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author upagge 11.02.2021
 */
public interface DiscussionRepository {

    /**
     * Вернуть все дискусии для MR
     */
    List<Discussion> findAllByMergeRequestId(Long mergeRequestId);

    Discussion save(Discussion discussion);

    Optional<Discussion> findById(String discussionId);

    List<Discussion> findAll();

    List<Discussion> findAllById(Set<String> discussionIds);

    Set<String> findAllIds();

    void deleteById(String id);

    void cleanOld();

}
