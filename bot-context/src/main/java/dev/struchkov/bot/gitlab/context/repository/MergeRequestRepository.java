package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequestForDiscussion;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MergeRequestRepository {

    Set<IdAndStatusPr> findAllIdByStateIn(@NonNull Set<MergeRequestState> states);

    MergeRequest save(MergeRequest mergeRequest);

    Optional<MergeRequest> findById(Long mergeRequestId);

    List<MergeRequestForDiscussion> findAllForDiscussion();

    List<MergeRequest> findAllById(Set<Long> mergeRequestIds);

    List<MergeRequest> findAllByReviewerId(Long personId);

    void deleteByStates(Set<MergeRequestState> states);

    Set<Long> findAllIds();

    void notification(boolean enable, Long mrId);

    void notificationByProjectId(boolean enable, Set<Long> projectIds);

}
