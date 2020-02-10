package com.tsc.bitbucketbot.service;

import com.tsc.bitbucketbot.domain.entity.PullRequest;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [31.01.2020]
 */
public interface PullRequestsService {

    @NonNull
    boolean existsByBitbucketIdAndReposId(Long bitbucketId, Long repositoryId);

    Set<PullRequest> getAllById(@NonNull Set<Long> pullRequestJsonId);

    List<PullRequest> addAll(@NonNull Collection<PullRequest> pullRequests);

    List<PullRequest> updateAll(@NonNull Collection<PullRequest> pullRequests);

    @NonNull
    Optional<Long> getIdByBitbucketIdAndReposId(Long bitbucketId, Long repositoryId);

    void deleteAll(@NonNull Set<Long> id);

    Optional<PullRequest> update(PullRequest pullRequest);

}
