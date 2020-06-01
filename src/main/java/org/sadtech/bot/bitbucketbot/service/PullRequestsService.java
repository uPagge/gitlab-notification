package org.sadtech.bot.bitbucketbot.service;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.IdAndStatusPr;
import org.sadtech.bot.bitbucketbot.domain.Pagination;
import org.sadtech.bot.bitbucketbot.domain.PullRequestStatus;
import org.sadtech.bot.bitbucketbot.domain.ReviewerStatus;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
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
    List<PullRequest> getAllByReviewerAndStatuses(@NonNull String login, @NonNull ReviewerStatus reviewerStatus, @NonNull Set<PullRequestStatus> pullRequestStatuses);

    List<PullRequest> getAllByAuthorAndReviewerStatus(@NonNull String login, @NonNull ReviewerStatus status);

    Set<Long> getAllId();

    Set<IdAndStatusPr> getAllId(Set<PullRequestStatus> statuses);

    Page<PullRequest> getAll(@NonNull Pagination pagination);

    List<PullRequest> getAllByAuthor(@NonNull String login, @NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo);

}
