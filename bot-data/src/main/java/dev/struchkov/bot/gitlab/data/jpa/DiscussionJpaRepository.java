package dev.struchkov.bot.gitlab.data.jpa;

import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * @author upagge 11.02.2021
 */
public interface DiscussionJpaRepository extends JpaRepository<Discussion, String> {

    /**
     * Находит все обсуждения MR
     */
    List<Discussion> findAllByMergeRequestId(Long mergeRequestId);

    @Query("SELECT d.id FROM Discussion d")
    Set<String> findAllIds();

    void removeAllByMergeRequestIsNull();

    @Modifying
    @Query("DELETE FROM Discussion d WHERE d.id = :id")
    void deleteById(@Param("id") String id);

    @Modifying
    @Query("UPDATE Discussion d SET d.notification = :enable WHERE d.id = :discussionId")
    void notification(@Param("enable") boolean enable, @Param("discussionId") String discussionId);

}
