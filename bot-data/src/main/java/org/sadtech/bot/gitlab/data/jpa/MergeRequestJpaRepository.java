package org.sadtech.bot.gitlab.data.jpa;

import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author upagge [31.01.2020]
 */

public interface MergeRequestJpaRepository extends JpaRepositoryImplementation<MergeRequest, Long> {

    Set<MergeRequest> findAllByIdIn(Set<Long> ids);

    void deleteAllByIdIn(Collection<Long> id);

    @Query("SELECT new org.sadtech.bot.gitlab.context.domain.IdAndStatusPr(p.id, p.twoId, p.projectId, p.state) FROM MergeRequest p WHERE p.state IN :states")
    Set<IdAndStatusPr> findAllIdByStateIn(@Param("states") Set<MergeRequestState> states);

    @Query("SELECT p.id from MergeRequest p")
    Set<Long> findAllIds();

    @Query("SELECT p.author.id from MergeRequest p WHERE p.id = :id")
    Optional<String> findAuthorById(@Param("id") Long id);

    List<MergeRequest> findAllByAssigneeId(Long userId);

}
