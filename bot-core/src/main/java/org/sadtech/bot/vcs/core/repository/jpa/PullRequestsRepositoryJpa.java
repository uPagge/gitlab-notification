package org.sadtech.bot.vcs.core.repository.jpa;

import org.sadtech.bot.vcs.core.domain.IdAndStatusPr;
import org.sadtech.bot.vcs.core.domain.PullRequestStatus;
import org.sadtech.bot.vcs.core.domain.ReviewerStatus;
import org.sadtech.bot.vcs.core.domain.entity.PullRequest;
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
public interface PullRequestsRepositoryJpa extends JpaRepositoryImplementation<PullRequest, Long> {

    Set<PullRequest> findAllByIdIn(Set<Long> ids);

    Boolean existsByBitbucketIdAndRepositoryId(Long bitbucketId, Long repositoryId);

    @Query("SELECT p.id FROM PullRequest p WHERE p.bitbucketId=:bitbucketId AND p.repositoryId=:repositoryId")
    Optional<Long> findIdByBitbucketIdAndRepositoryId(@Param("bitbucketId") Long bitbucketId, @Param("repositoryId") Long repositoryId);

    void deleteAllByIdIn(Collection<Long> id);

    @Query("SELECT p FROM PullRequest p LEFT JOIN p.reviewers r WHERE r.personLogin=:reviewer AND r.status =:reviewerStatus AND p.status IN :pullRequestStatus")
    List<PullRequest> findAllByReviewerAndStatuses(@Param("reviewer") String reviewer, @Param("reviewerStatus") ReviewerStatus reviewerStatus, @Param("pullRequestStatus") Set<PullRequestStatus> pullRequestStatus);

    @Query("SELECT p FROM PullRequest p LEFT JOIN p.reviewers r WHERE p.authorLogin=:author AND r.status=:reviewerStatus")
    List<PullRequest> findAllByAuthorAndReviewerStatus(@Param("author") String author, @Param("reviewerStatus") ReviewerStatus reviewerStatus);

    //    @Query("SELECT new org.sadtech.bot.bitbucketbot.domain.IdAndStatusPr(p.id, p.status) FROM PullRequest p WHERE p.status IN :statuses")
    Set<IdAndStatusPr> findAllIdByStatusIn(@Param("statuses") Set<PullRequestStatus> statuses);

    @Query("SELECT p.id from PullRequest p")
    Set<Long> findAllIds();

    @Query("SELECT p.authorLogin from PullRequest p WHERE p.id = :id")
    Optional<String> findAuthorById(@Param("id") Long id);

//    @Query("SELECT p FROM PullRequest p WHERE p.authorLogin = :login AND p.createDate BETWEEN :dateFrom AND :dateTo")
//    List<PullRequest> findAllByAuthorAndDateBetween(@Param("login") String login, @Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);

}
