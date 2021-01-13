package org.sadtech.bot.vcs.bitbucketbot.data.impl;

import lombok.NonNull;
import org.sadtech.bot.vcs.bitbucketbot.data.jpa.PullRequestMiniRepositoryJpa;
import org.sadtech.bot.vcs.bitbucketbot.data.jpa.PullRequestsRepositoryJpa;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.IdAndStatusPr;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.PullRequest;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.PullRequestMini;
import org.sadtech.bot.vsc.bitbucketbot.context.repository.PullRequestsRepository;
import org.sadtech.bot.vsc.context.domain.PullRequestStatus;
import org.sadtech.bot.vsc.context.domain.ReviewerStatus;
import org.sadtech.haiti.database.repository.manager.FilterManagerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class PullRequestsRepositoryImpl extends FilterManagerRepository<PullRequest, Long> implements PullRequestsRepository {

    private final PullRequestsRepositoryJpa repositoryJpa;
    private final PullRequestMiniRepositoryJpa pullRequestMiniRepositoryJpa;

    public PullRequestsRepositoryImpl(PullRequestsRepositoryJpa jpaRepository, PullRequestMiniRepositoryJpa pullRequestMiniRepositoryJpa) {
        super(jpaRepository);
        repositoryJpa = jpaRepository;
        this.pullRequestMiniRepositoryJpa = pullRequestMiniRepositoryJpa;
    }

    @Override
    public List<PullRequest> findAllByReviewerAndStatuses(String login, ReviewerStatus reviewerStatus, Set<PullRequestStatus> statuses) {
        return repositoryJpa.findAllByReviewerAndStatuses(login, reviewerStatus, statuses);
    }

    @Override
    public List<PullRequest> findAllByAuthorAndReviewerStatus(String login, ReviewerStatus status) {
        return repositoryJpa.findAllByAuthorAndReviewerStatus(login, status);
    }

    @Override
    public Set<IdAndStatusPr> findAllIdByStatusIn(Set<PullRequestStatus> statuses) {
        return repositoryJpa.findAllIdByStatusIn(statuses);
    }

    @Override
    public Optional<PullRequestMini> findMiniInfoById(@NonNull Long id) {
        return pullRequestMiniRepositoryJpa.findById(id);
    }

}
