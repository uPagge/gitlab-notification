package com.tsc.bitbucketbot.service.impl;

import com.tsc.bitbucketbot.domain.entity.PullRequest;
import com.tsc.bitbucketbot.domain.entity.Reviewer;
import com.tsc.bitbucketbot.repository.PullRequestsRepository;
import com.tsc.bitbucketbot.service.PullRequestsService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    public Set<Long> existsById(@NonNull Set<Long> idList) {
        return idList.stream()
                .filter(pullRequestsRepository::existsById)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Long> existsAllById(@NonNull Set<Long> pullRequestJsonId) {
        return pullRequestJsonId.stream().filter(pullRequestsRepository::existsById).collect(Collectors.toSet());
    }

    @Override
    public List<PullRequest> addAll(Set<PullRequest> pullRequests) {
        return pullRequestsRepository.saveAll(pullRequests);
    }

    @Override
    @Transactional
    public Optional<PullRequest> addReviewer(@NonNull Long pullRequestId, @NonNull List<Reviewer> reviewers) {
        final Optional<PullRequest> optPullRequest = pullRequestsRepository.findById(pullRequestId);
        if (optPullRequest.isPresent()) {
            final PullRequest pullRequest = optPullRequest.get();
            pullRequest.setReviewers(reviewers);
            return Optional.of(pullRequestsRepository.save(pullRequest));
        }
        return Optional.empty();
    }

}
