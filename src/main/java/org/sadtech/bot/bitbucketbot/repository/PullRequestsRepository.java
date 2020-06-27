package org.sadtech.bot.bitbucketbot.repository;

import org.sadtech.basic.context.repository.BusinessLogicRepository;
import org.sadtech.bot.bitbucketbot.domain.IdAndStatusPr;
import org.sadtech.bot.bitbucketbot.domain.PullRequestStatus;
import org.sadtech.bot.bitbucketbot.domain.ReviewerStatus;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;

import java.util.List;
import java.util.Set;

public interface PullRequestsRepository extends BusinessLogicRepository<PullRequest, Long> {

    List<PullRequest> findAllByReviewerAndStatuses(String login, ReviewerStatus reviewerStatus, Set<PullRequestStatus> statuses);

    List<PullRequest> findAllByAuthorAndReviewerStatus(String login, ReviewerStatus status);

    Set<IdAndStatusPr> findAllIdByStatusIn(Set<PullRequestStatus> statuses);

}
