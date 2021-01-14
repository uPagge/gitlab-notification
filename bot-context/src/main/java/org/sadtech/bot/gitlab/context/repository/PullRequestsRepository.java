package org.sadtech.bot.gitlab.context.repository;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.entity.PullRequestMini;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;
import org.sadtech.haiti.filter.FilterOperation;

import java.util.Optional;
import java.util.Set;

public interface PullRequestsRepository extends SimpleManagerRepository<MergeRequest, Long>, FilterOperation<MergeRequest> {

    Set<IdAndStatusPr> findAllIdByStatusIn(Set<MergeRequestState> statuses);

    Optional<PullRequestMini> findMiniInfoById(@NonNull Long id);

}
