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

    Set<Long> existsById(@NonNull final Set<Long> pullRequestId);

    Set<Long> existsAllIdById(@NonNull Set<Long> pullRequestId);

    Set<PullRequest> getAllById(@NonNull Set<Long> pullRequestJsonId);

    List<PullRequest> addAll(@NonNull Set<PullRequest> pullRequests);

    List<PullRequest> updateAll(@NonNull List<PullRequest> pullRequests);

}
