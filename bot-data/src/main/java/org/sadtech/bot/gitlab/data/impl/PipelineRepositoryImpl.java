package org.sadtech.bot.gitlab.data.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.PipelineStatus;
import org.sadtech.bot.gitlab.context.domain.entity.Pipeline;
import org.sadtech.bot.gitlab.context.repository.PipelineRepository;
import org.sadtech.bot.gitlab.data.jpa.PipelineJpaRepository;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.sadtech.haiti.database.util.Converter;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * // TODO: 17.01.2021 Добавить описание.
 *
 * @author upagge 17.01.2021
 */
@Repository
public class PipelineRepositoryImpl extends AbstractSimpleManagerRepository<Pipeline, Long> implements PipelineRepository {

    private final PipelineJpaRepository jpaRepository;

    public PipelineRepositoryImpl(PipelineJpaRepository jpaRepository) {
        super(jpaRepository);
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Sheet<Pipeline> findAllByStatuses(@NonNull Set<PipelineStatus> statuses, @NonNull Pagination pagination) {
        return Converter.page(
                jpaRepository.findAllByStatusIn(statuses, Converter.pagination(pagination))
        );
    }
}
