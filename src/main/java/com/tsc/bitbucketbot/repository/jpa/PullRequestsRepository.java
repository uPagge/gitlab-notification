package com.tsc.bitbucketbot.repository.jpa;

import com.tsc.bitbucketbot.domain.IdAndStatusPr;
import com.tsc.bitbucketbot.domain.PullRequestStatus;
import com.tsc.bitbucketbot.domain.ReviewerStatus;
import com.tsc.bitbucketbot.domain.entity.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author upagge [31.01.2020]
 */
public interface PullRequestsRepository extends JpaRepository<PullRequest, Long> {

    Set<PullRequest> findAllByIdIn(Set<Long> ids);

    Boolean existsByBitbucketIdAndRepositoryId(Long bitbucketId, Long repositoryId);

    @Query("SELECT p.id FROM PullRequest p WHERE p.bitbucketId=:bitbucketId AND p.repositoryId=:repositoryId")
    Optional<Long> findIdByBitbucketIdAndRepositoryId(@Param("bitbucketId") Long bitbucketId, @Param("repositoryId") Long repositoryId);

    void deleteAllByIdIn(Collection<Long> id);

    @Query("SELECT p FROM PullRequest p LEFT JOIN p.reviewers r WHERE r.user=:reviewer AND r.status =:status")
    List<PullRequest> findAllByReviewerAndStatuses(@Param("reviewer") String reviewer, @Param("status") ReviewerStatus status);

    @Query("SELECT p FROM PullRequest p LEFT JOIN p.reviewers r WHERE p.author.login=:author AND r.status=:reviewerStatus")
    List<PullRequest> findAllByAuthorAndReviewerStatus(@Param("author") String author, @Param("reviewerStatus") ReviewerStatus reviewerStatus);

    @Query("SELECT new com.tsc.bitbucketbot.domain.IdAndStatusPr(p.id, p.status) FROM PullRequest p WHERE p.status IN :statuses")
    Set<IdAndStatusPr> findAllIdByStatusIn(@Param("statuses") Set<PullRequestStatus> statuses);

    @Query("SELECT p.id from PullRequest p")
    Set<Long> findAllIds();

    @Query("SELECT p FROM PullRequest p WHERE p.author.login = :login AND p.createDate BETWEEN :dateFrom AND :dateTo")
    List<PullRequest> findAllByAuthorAndDateBetween(@Param("login") String login, @Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);

}
