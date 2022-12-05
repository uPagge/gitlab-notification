package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.ExistsContainer;
import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.filter.MergeRequestFilter;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface MergeRequestsService {

    MergeRequest create(@NonNull MergeRequest mergeRequest);

    MergeRequest update(@NonNull MergeRequest mergeRequest);

    List<MergeRequest> updateAll(@NonNull List<MergeRequest> mergeRequests);

    /**
     * Получить все идентификаторы вместе со статусами.
     *
     * @param statuses Статусы ПРов
     * @return Объект, содержащий идентификатор и статус ПР
     */
    Set<IdAndStatusPr> getAllId(Set<MergeRequestState> statuses);

    Page<MergeRequest> getAll(Pageable pagination);

    Page<MergeRequest> getAll(@NonNull MergeRequestFilter filter, Pageable pagination);

    ExistsContainer<MergeRequest, Long> existsById(@NonNull Set<Long> mergeRequestIds);

    List<MergeRequest> createAll(List<MergeRequest> newMergeRequests);

    void deleteAllById(@NonNull Set<Long> mergeRequestIds);

}
