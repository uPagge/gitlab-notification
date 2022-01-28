package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import dev.struchkov.bot.gitlab.context.repository.PipelineRepository;
import dev.struchkov.bot.gitlab.data.jpa.PipelineJpaRepository;
import dev.struchkov.haiti.context.page.Pagination;
import dev.struchkov.haiti.context.page.Sheet;
import dev.struchkov.haiti.database.repository.manager.FilterManagerRepository;
import dev.struchkov.haiti.database.util.Converter;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author upagge 17.01.2021
 */
@Repository
public class PipelineRepositoryImpl extends FilterManagerRepository<Pipeline, Long> implements PipelineRepository {

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
