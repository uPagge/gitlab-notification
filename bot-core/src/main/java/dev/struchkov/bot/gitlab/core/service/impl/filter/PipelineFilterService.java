package dev.struchkov.bot.gitlab.core.service.impl.filter;

import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import dev.struchkov.bot.gitlab.context.domain.entity.PipelineFields;
import dev.struchkov.bot.gitlab.context.domain.filter.PipelineFilter;
import dev.struchkov.bot.gitlab.context.repository.PipelineRepository;
import dev.struchkov.haiti.filter.Filter;
import dev.struchkov.haiti.filter.FilterQuery;
import dev.struchkov.haiti.filter.criteria.CriteriaFilter;
import dev.struchkov.haiti.filter.criteria.CriteriaQuery;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Сервис фильтрации пайплайнов.
 *
 * @author upagge 08.02.2021
 */
@Service
@RequiredArgsConstructor
public class PipelineFilterService {

    private final PipelineRepository pipelineRepository;

    public Page<Pipeline> getAll(PipelineFilter filter, Pageable pagination) {
        return pipelineRepository.filter(createFilter(filter), pagination);
    }

    private Filter createFilter(@NonNull PipelineFilter pipelineFilter) {
        return CriteriaFilter.<Pipeline>create()
                .and(convertAnd(pipelineFilter));
    }

    private FilterQuery convertAnd(PipelineFilter pipelineFilter) {
        return CriteriaQuery.<Pipeline>create()
                .lessThan(PipelineFields.created, pipelineFilter.getLessThanCreatedDate());
    }


}
