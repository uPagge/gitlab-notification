package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import dev.struchkov.bot.gitlab.context.domain.filter.MergeRequestFilter;
import dev.struchkov.bot.gitlab.context.domain.filter.PipelineFilter;
import dev.struchkov.bot.gitlab.context.service.CleanService;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.bot.gitlab.context.service.PipelineService;
import dev.struchkov.haiti.context.page.Sheet;
import dev.struchkov.haiti.context.page.impl.PaginationImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.struchkov.bot.gitlab.context.domain.MergeRequestState.CLOSED;
import static dev.struchkov.bot.gitlab.context.domain.MergeRequestState.MERGED;

/**
 * // TODO: 08.02.2021 Добавить описание.
 *
 * @author upagge 08.02.2021
 */
@Service
@RequiredArgsConstructor
public class CleanServiceImpl implements CleanService {

    private static final int COUNT = 1000;
    private static final MergeRequestFilter CLEAN_FILTER = MergeRequestFilter.builder()
            .states(Set.of(MERGED, CLOSED))
            .build();

    private final MergeRequestsService mergeRequestsService;
    private final PipelineService pipelineService;

    @Override
    public void cleanMergedPullRequests() {
        int page = 0;
        Sheet<MergeRequest> mergeRequestSheet = mergeRequestsService.getAll(CLEAN_FILTER, PaginationImpl.of(page, COUNT));

        while (mergeRequestSheet.hasContent()) {
            final Set<Long> ids = mergeRequestSheet.getContent().stream()
                    .map(MergeRequest::getId)
                    .collect(Collectors.toUnmodifiableSet());

            mergeRequestsService.deleteAllById(ids);

            mergeRequestSheet = mergeRequestsService.getAll(CLEAN_FILTER, PaginationImpl.of(++page, COUNT));
        }
    }

    @Override
    public void cleanOldPipelines() {
        int page = 0;
        final PipelineFilter filter = cleanPipelineFilter();
        Sheet<Pipeline> sheet = pipelineService.getAll(filter, PaginationImpl.of(page, COUNT));

        while (sheet.hasContent()) {
            final Set<Long> ids = sheet.getContent().stream()
                    .map(Pipeline::getId)
                    .collect(Collectors.toUnmodifiableSet());

            pipelineService.deleteAllById(ids);

            sheet = pipelineService.getAll(filter, PaginationImpl.of(page, COUNT));
        }
    }

    private PipelineFilter cleanPipelineFilter() {
        final PipelineFilter pipelineFilter = new PipelineFilter();
        pipelineFilter.setLessThanCreatedDate(LocalDateTime.now().minusDays(1L));
        return pipelineFilter;
    }

}
