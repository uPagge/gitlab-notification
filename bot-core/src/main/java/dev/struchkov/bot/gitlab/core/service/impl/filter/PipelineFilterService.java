package dev.struchkov.bot.gitlab.core.service.impl.filter;

import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline_;
import dev.struchkov.bot.gitlab.context.domain.filter.PipelineFilter;
import dev.struchkov.bot.gitlab.context.repository.PipelineRepository;
import dev.struchkov.haiti.core.service.AbstractFilterService;
import dev.struchkov.haiti.filter.Filter;
import dev.struchkov.haiti.filter.FilterQuery;
import dev.struchkov.haiti.filter.criteria.CriteriaFilter;
import dev.struchkov.haiti.filter.criteria.CriteriaQuery;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * // TODO: 08.02.2021 Добавить описание.
 *
 * @author upagge 08.02.2021
 */
@Service
public class PipelineFilterService extends AbstractFilterService<Pipeline, PipelineFilter> {

    public PipelineFilterService(PipelineRepository pipelineRepository) {
        super(pipelineRepository);
    }

    @Override
    protected Filter createFilter(@NonNull PipelineFilter pipelineFilter) {
        return CriteriaFilter.<Pipeline>create()
                .and(convertAnd(pipelineFilter));
    }

    private FilterQuery convertAnd(PipelineFilter pipelineFilter) {
        return CriteriaQuery.<Pipeline>create()
                .lessThan(Pipeline_.CREATED, pipelineFilter.getLessThanCreatedDate());
    }
}
