package dev.struchkov.bot.gitlab.data.jpa;

import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
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

    void deleteAllByIdIn(Collection<Long> id);

    @Query("SELECT new dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr(p.id, p.twoId, p.projectId, p.state) FROM MergeRequest p WHERE p.state IN :states")
    Set<IdAndStatusPr> findAllIdByStateIn(@Param("states") Set<MergeRequestState> states);

    @Query("SELECT p.id FROM MergeRequest p")
    Set<Long> findAllIds();

    @Query("SELECT p.author.id FROM MergeRequest p WHERE p.id = :id")
    Optional<String> findAuthorById(@Param("id") Long id);

    List<MergeRequest> findAllByAssigneeId(Long userId);

    @Query("SELECT mr FROM MergeRequest mr LEFT JOIN mr.reviewers r WHERE r.id = :reviewerId")
    List<MergeRequest> findAllByReviewersIn(@Param("reviewerId") Long reviewerId);

}
