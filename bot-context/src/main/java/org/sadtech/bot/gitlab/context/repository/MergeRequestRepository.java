package org.sadtech.bot.gitlab.context.repository;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;
import org.sadtech.haiti.filter.FilterOperation;

import java.util.List;
import java.util.Set;

public interface MergeRequestRepository extends SimpleManagerRepository<MergeRequest, Long>, FilterOperation<MergeRequest> {

    Set<IdAndStatusPr> findAllIdByStateIn(Set<MergeRequestState> states);

    List<MergeRequest> findAllByAssignee(@NonNull Long userId);

}
