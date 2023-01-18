package dev.struchkov.bot.gitlab.data.jpa;

import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * @author upagge [31.01.2020]
 */

public interface MergeRequestJpaRepository extends JpaRepositoryImplementation<MergeRequest, Long> {

    @Query("SELECT new dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr(p.id, p.twoId, p.projectId, p.state) FROM MergeRequest p WHERE p.state IN :states")
    Set<IdAndStatusPr> findAllIdByStateIn(@Param("states") Set<MergeRequestState> states);

    @Query("SELECT mr FROM MergeRequest mr LEFT JOIN mr.reviewers r WHERE r.id = :reviewerId")
    List<MergeRequest> findAllByReviewersIn(@Param("reviewerId") Long reviewerId);

    void deleteAllByStateIn(Set<MergeRequestState> states);

    @Query("SELECT mr.id FROM MergeRequest mr")
    Set<Long> findAllIds();

    @Modifying
    @Query("UPDATE MergeRequest mr SET mr.notification = false WHERE mr.id = :mrId")
    void disableNotify(@Param("mrId") Long mrId);

}
