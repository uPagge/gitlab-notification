package dev.struchkov.bot.gitlab.data.jpa;

import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author upagge 11.02.2021
 */
public interface DiscussionJpaRepository extends JpaRepository<Discussion, String> {

    /**
     * Находит все обсуждения MR
     */
    List<Discussion> findAllByMergeRequestId(Long mergeRequestId);

}
