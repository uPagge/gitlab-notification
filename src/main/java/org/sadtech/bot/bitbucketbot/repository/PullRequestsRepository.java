package org.sadtech.bot.bitbucketbot.repository;

import lombok.NonNull;
import org.sadtech.basic.context.repository.SimpleManagerRepository;
import org.sadtech.basic.context.repository.simple.FilterOperation;
import org.sadtech.bot.bitbucketbot.domain.IdAndStatusPr;
import org.sadtech.bot.bitbucketbot.domain.PullRequestStatus;
import org.sadtech.bot.bitbucketbot.domain.ReviewerStatus;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequestMini;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PullRequestsRepository extends SimpleManagerRepository<PullRequest, Long>, FilterOperation<PullRequest> {

    List<PullRequest> findAllByReviewerAndStatuses(String login, ReviewerStatus reviewerStatus, Set<PullRequestStatus> statuses);

    List<PullRequest> findAllByAuthorAndReviewerStatus(String login, ReviewerStatus status);

    Set<IdAndStatusPr> findAllIdByStatusIn(Set<PullRequestStatus> statuses);

    Optional<PullRequestMini> findMiniInfoById(@NonNull Long id);

}
