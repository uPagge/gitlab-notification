package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.haiti.filter.Filter;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MergeRequestRepository {

    Set<IdAndStatusPr> findAllIdByStateIn(@NonNull Set<MergeRequestState> states);

    MergeRequest save(MergeRequest mergeRequest);

    Optional<MergeRequest> findById(Long mergeRequestId);

    List<MergeRequest> findAll();

    List<MergeRequest> findAllById(Set<Long> mergeRequestIds);

    void deleteByIds(Set<Long> mergeRequestIds);

    Page<MergeRequest> filter(Filter filter, Pageable pageable);

    List<MergeRequest> findAllByReviewerId(Long personId);

}
