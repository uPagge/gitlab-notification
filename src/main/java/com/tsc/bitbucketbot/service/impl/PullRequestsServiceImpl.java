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
    public Set<Long> existsAllIdById(@NonNull Set<Long> pullRequestJsonId) {
        return pullRequestJsonId.stream().filter(pullRequestsRepository::existsById).collect(Collectors.toSet());
    }

    @Override
    public Set<PullRequest> getAllById(@NonNull Set<Long> pullRequestJsonId) {
        return pullRequestsRepository.findAllByIdIn(pullRequestJsonId);
    }

    @Override
    public List<PullRequest> addAll(@NonNull Set<PullRequest> pullRequests) {
        return pullRequestsRepository.saveAll(pullRequests);
    }

    @Override
    public List<PullRequest> updateAll(@NonNull List<PullRequest> pullRequests) {
        return pullRequestsRepository.saveAll(pullRequests);
    }

}
