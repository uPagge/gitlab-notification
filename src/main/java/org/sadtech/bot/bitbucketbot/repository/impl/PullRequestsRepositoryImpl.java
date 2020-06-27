package org.sadtech.bot.bitbucketbot.repository.impl;

import org.sadtech.basic.database.repository.AbstractBusinessLogicJpaRepository;
import org.sadtech.bot.bitbucketbot.domain.IdAndStatusPr;
import org.sadtech.bot.bitbucketbot.domain.PullRequestStatus;
import org.sadtech.bot.bitbucketbot.domain.ReviewerStatus;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.repository.PullRequestsRepository;
import org.sadtech.bot.bitbucketbot.repository.jpa.PullRequestsRepositoryJpa;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public class PullRequestsRepositoryImpl extends AbstractBusinessLogicJpaRepository<PullRequest, Long> implements PullRequestsRepository {

    private final PullRequestsRepositoryJpa pullRequestsRepositoryJpa;

    protected PullRequestsRepositoryImpl(PullRequestsRepositoryJpa pullRequestsRepositoryJpa) {
        super(pullRequestsRepositoryJpa);
        this.pullRequestsRepositoryJpa = pullRequestsRepositoryJpa;
    }

    @Override
    public List<PullRequest> findAllByReviewerAndStatuses(String login, ReviewerStatus reviewerStatus, Set<PullRequestStatus> statuses) {
        return pullRequestsRepositoryJpa.findAllByReviewerAndStatuses(login, reviewerStatus, statuses);
    }

    @Override
    public List<PullRequest> findAllByAuthorAndReviewerStatus(String login, ReviewerStatus status) {
        return pullRequestsRepositoryJpa.findAllByAuthorAndReviewerStatus(login, status);
    }

    @Override
    public Set<IdAndStatusPr> findAllIdByStatusIn(Set<PullRequestStatus> statuses) {
        return pullRequestsRepositoryJpa.findAllIdByStatusIn(statuses);
    }

}
