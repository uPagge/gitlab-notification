package org.sadtech.bot.gitlab.context.repository;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.entity.PullRequest;
import org.sadtech.bot.gitlab.context.domain.entity.PullRequestMini;
import org.sadtech.bot.vsc.context.domain.PullRequestStatus;
import org.sadtech.bot.vsc.context.domain.ReviewerStatus;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;
import org.sadtech.haiti.filter.FilterOperation;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PullRequestsRepository extends SimpleManagerRepository<PullRequest, Long>, FilterOperation<PullRequest> {

    List<PullRequest> findAllByReviewerAndStatuses(String login, ReviewerStatus reviewerStatus, Set<PullRequestStatus> statuses);

    List<PullRequest> findAllByAuthorAndReviewerStatus(String login, ReviewerStatus status);

    Set<IdAndStatusPr> findAllIdByStatusIn(Set<PullRequestStatus> statuses);

    Optional<PullRequestMini> findMiniInfoById(@NonNull Long id);

}
