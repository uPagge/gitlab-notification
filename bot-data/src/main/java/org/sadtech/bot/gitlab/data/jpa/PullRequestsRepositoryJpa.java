package org.sadtech.bot.gitlab.data.jpa;

import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * @author upagge [31.01.2020]
 */

@NoRepositoryBean
public interface PullRequestsRepositoryJpa extends JpaRepositoryImplementation<MergeRequest, Long> {

    Set<MergeRequest> findAllByIdIn(Set<Long> ids);

    Boolean existsByBitbucketIdAndRepositoryId(Long bitbucketId, Long repositoryId);

    //    @Query("SELECT p.id FROM PullRequest p WHERE p.bitbucketId=:bitbucketId AND p.repositoryId=:repositoryId")
    Optional<Long> findIdByBitbucketIdAndRepositoryId(@Param("bitbucketId") Long bitbucketId, @Param("repositoryId") Long repositoryId);

    void deleteAllByIdIn(Collection<Long> id);

    //    @Query("SELECT new org.sadtech.bot.bitbucketbot.domain.IdAndStatusPr(p.id, p.status) FROM PullRequest p WHERE p.status IN :statuses")
    Set<IdAndStatusPr> findAllIdByStatusIn(@Param("statuses") Set<MergeRequestState> statuses);

    //    @Query("SELECT p.id from PullRequest p")
    Set<Long> findAllIds();

    //    @Query("SELECT p.authorLogin from PullRequest p WHERE p.id = :id")
    Optional<String> findAuthorById(@Param("id") Long id);

}
