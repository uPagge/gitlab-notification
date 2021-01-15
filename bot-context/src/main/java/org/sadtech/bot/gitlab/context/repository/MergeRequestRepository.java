package org.sadtech.bot.gitlab.context.repository;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequestMini;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;
import org.sadtech.haiti.filter.FilterOperation;

import java.util.Optional;
import java.util.Set;

public interface MergeRequestRepository extends SimpleManagerRepository<MergeRequest, Long>, FilterOperation<MergeRequest> {

    Set<IdAndStatusPr> findAllIdByStatusIn(Set<MergeRequestState> statuses);

    Optional<MergeRequestMini> findMiniInfoById(@NonNull Long id);

}
