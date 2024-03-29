package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequestForDiscussion;
import lombok.NonNull;

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

    List<MergeRequestForDiscussion> getAllForDiscussion();

    ExistContainer<MergeRequest, Long> existsById(@NonNull Set<Long> mergeRequestIds);

    List<MergeRequest> createAll(List<MergeRequest> newMergeRequests);

    List<MergeRequest> getAllByReviewerId(@NonNull Long personId);

    void cleanOld();

    Set<Long> getAllIds();

    void notification(boolean enable, @NonNull Long mrId);

    void notificationByProjectId(boolean enable, @NonNull Set<Long> projectIds);

}
