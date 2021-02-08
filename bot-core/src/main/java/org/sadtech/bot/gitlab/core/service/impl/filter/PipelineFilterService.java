package org.sadtech.bot.gitlab.core.service.impl.filter;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Pipeline;
import org.sadtech.bot.gitlab.context.domain.entity.Pipeline_;
import org.sadtech.bot.gitlab.context.domain.filter.PipelineFilter;
import org.sadtech.bot.gitlab.context.repository.PipelineRepository;
import org.sadtech.haiti.core.service.AbstractFilterService;
import org.sadtech.haiti.filter.Filter;
import org.sadtech.haiti.filter.FilterQuery;
import org.sadtech.haiti.filter.criteria.CriteriaFilter;
import org.sadtech.haiti.filter.criteria.CriteriaQuery;
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
