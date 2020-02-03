package com.tsc.bitbucketbot.service;

import com.tsc.bitbucketbot.domain.entity.PullRequest;
import com.tsc.bitbucketbot.domain.entity.Reviewer;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [31.01.2020]
 */
public interface PullRequestsService {

    Set<Long> existsById(@NonNull final Set<Long> idList);

    Set<Long> existsAllById(@NonNull Set<Long> pullRequestJsonId);

    List<PullRequest> addAll(Set<PullRequest> pullRequests);

    Optional<PullRequest> addReviewer(@NonNull Long pullRequestJsonId, @NonNull List<Reviewer> reviewers);
}
