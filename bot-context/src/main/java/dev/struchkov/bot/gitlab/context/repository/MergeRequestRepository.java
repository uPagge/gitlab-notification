package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.haiti.context.repository.SimpleManagerRepository;
import dev.struchkov.haiti.filter.FilterOperation;
import lombok.NonNull;

import java.util.List;
import java.util.Set;

public interface MergeRequestRepository extends SimpleManagerRepository<MergeRequest, Long>, FilterOperation<MergeRequest> {

    Set<IdAndStatusPr> findAllIdByStateIn(@NonNull Set<MergeRequestState> states);

    //TODO [28.01.2022]: Решить, нужно ли оставить
    List<MergeRequest> findAllByAssignee(@NonNull Long userId);

}
