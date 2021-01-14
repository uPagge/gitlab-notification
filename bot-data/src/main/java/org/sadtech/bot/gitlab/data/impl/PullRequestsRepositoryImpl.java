package org.sadtech.bot.gitlab.data.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.entity.PullRequestMini;
import org.sadtech.bot.gitlab.context.repository.PullRequestsRepository;
import org.sadtech.bot.gitlab.data.jpa.PullRequestMiniRepositoryJpa;
import org.sadtech.bot.gitlab.data.jpa.PullRequestsRepositoryJpa;
import org.sadtech.haiti.database.repository.manager.FilterManagerRepository;

import java.util.Optional;
import java.util.Set;

//@Repository
public class PullRequestsRepositoryImpl extends FilterManagerRepository<MergeRequest, Long> implements PullRequestsRepository {

    private final PullRequestsRepositoryJpa repositoryJpa;
    private final PullRequestMiniRepositoryJpa pullRequestMiniRepositoryJpa;

    public PullRequestsRepositoryImpl(PullRequestsRepositoryJpa jpaRepository, PullRequestMiniRepositoryJpa pullRequestMiniRepositoryJpa) {
        super(jpaRepository);
        repositoryJpa = jpaRepository;
        this.pullRequestMiniRepositoryJpa = pullRequestMiniRepositoryJpa;
    }


    @Override
    public Set<IdAndStatusPr> findAllIdByStatusIn(Set<MergeRequestState> statuses) {
        return repositoryJpa.findAllIdByStatusIn(statuses);
    }

    @Override
    public Optional<PullRequestMini> findMiniInfoById(@NonNull Long id) {
        return pullRequestMiniRepositoryJpa.findById(id);
    }

}
