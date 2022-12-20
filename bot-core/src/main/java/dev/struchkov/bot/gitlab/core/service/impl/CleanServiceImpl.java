package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.filter.MergeRequestFilter;
import dev.struchkov.bot.gitlab.context.service.CleanService;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.bot.gitlab.context.domain.MergeRequestState.CLOSED;
import static dev.struchkov.bot.gitlab.context.domain.MergeRequestState.MERGED;

/**
 * Реализация сервиса очистки данных.
 *
 * @author upagge 08.02.2021
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CleanServiceImpl implements CleanService {

    private static final int COUNT = 1000;
    private static final MergeRequestFilter MR_CLEAN_FILTER = MergeRequestFilter.builder()
            .states(Set.of(MERGED, CLOSED))
            .build();

    private final MergeRequestsService mergeRequestsService;

    @Override
    public void cleanOldMergedRequests() {
        log.debug("Старт очистки старых MR");
        int page = 0;
        Page<MergeRequest> mergeRequestSheet = mergeRequestsService.getAll(MR_CLEAN_FILTER, PageRequest.of(page, COUNT));

        while (mergeRequestSheet.hasContent()) {
            final Set<Long> ids = mergeRequestSheet.getContent().stream()
                    .map(MergeRequest::getId)
                    .collect(Collectors.toUnmodifiableSet());

            mergeRequestsService.deleteAllById(ids);

            mergeRequestSheet = mergeRequestsService.getAll(MR_CLEAN_FILTER, PageRequest.of(++page, COUNT));
        }
        log.debug("Конец очистки старых MR");
    }

}
