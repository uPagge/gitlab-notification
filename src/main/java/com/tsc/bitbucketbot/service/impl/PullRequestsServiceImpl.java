package com.tsc.bitbucketbot.service.impl;

import com.tsc.bitbucketbot.domain.entity.PullRequest;
import com.tsc.bitbucketbot.repository.jpa.PullRequestsRepository;
import com.tsc.bitbucketbot.service.PullRequestsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [31.01.2020]
 */
@Service
@RequiredArgsConstructor
public class PullRequestsServiceImpl implements PullRequestsService {

    private final PullRequestsRepository pullRequestsRepository;

    @Override
    public boolean existsByBitbucketIdAndReposId(@NonNull Long bitbucketId, @NonNull Long repositoryId) {
        return pullRequestsRepository.existsByBitbucketIdAndRepositoryId(bitbucketId, repositoryId);
    }

    @Override
    public Set<PullRequest> getAllById(@NonNull Set<Long> pullRequestJsonId) {
        return pullRequestsRepository.findAllByIdIn(pullRequestJsonId);
    }

    @Override
    @Transactional
    public List<PullRequest> addAll(@NonNull Collection<PullRequest> pullRequests) {
        return pullRequestsRepository.saveAll(pullRequests);
    }

    @Override
    public List<PullRequest> updateAll(@NonNull Collection<PullRequest> pullRequests) {
        final List<PullRequest> updatePullRequests = pullRequests.stream()
                .filter(pullRequest -> pullRequestsRepository.existsById(pullRequest.getId()))
                .collect(Collectors.toList());
        return pullRequestsRepository.saveAll(updatePullRequests);
    }

    @Override
    public Optional<Long> getIdByBitbucketIdAndReposId(@NonNull Long bitbucketId, @NonNull Long repositoryId) {
        return pullRequestsRepository.findIdByBitbucketIdAndRepositoryId(bitbucketId, repositoryId);
    }

    @Override
    @Transactional
    public void deleteAll(@NonNull Set<Long> id) {
        pullRequestsRepository.deleteAllByIdIn(id);
    }

    @Override
    public List<PullRequest> getAllByReviewer(@NonNull String login) {
        return pullRequestsRepository.findAllByReviewers(login);
    }

    @Override
    public Set<Long> getAllId() {
        return pullRequestsRepository.getAllIds();
    }

}
