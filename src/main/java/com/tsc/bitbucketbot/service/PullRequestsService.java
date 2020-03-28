package com.tsc.bitbucketbot.service;

import com.tsc.bitbucketbot.domain.Pagination;
import com.tsc.bitbucketbot.domain.ReviewerStatus;
import com.tsc.bitbucketbot.domain.entity.PullRequest;
import lombok.NonNull;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PullRequestsService {

    @NonNull
    boolean existsByBitbucketIdAndReposId(Long bitbucketId, Long repositoryId);

    Set<PullRequest> getAllById(@NonNull Set<Long> pullRequestJsonId);

    List<PullRequest> addAll(@NonNull Collection<PullRequest> pullRequests);

    List<PullRequest> updateAll(@NonNull Collection<PullRequest> pullRequests);

    @NonNull
    Optional<Long> getIdByBitbucketIdAndReposId(Long bitbucketId, Long repositoryId);

    void deleteAll(@NonNull Set<Long> id);

    @NonNull
    List<PullRequest> getAllByReviewerAndStatuses(String login, ReviewerStatus statuses);

    List<PullRequest> getAllByAuthorAndReviewerStatus(@NonNull String login, @NonNull ReviewerStatus status);

    Set<Long> getAllId();

    Page<PullRequest> getAll(@NonNull Pagination pagination);

}
