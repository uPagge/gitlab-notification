package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import dev.struchkov.bot.gitlab.context.domain.filter.PipelineFilter;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * Сервис для работы с пайплайнами
 *
 * @author upagge 17.01.2021
 */
public interface PipelineService {

    Pipeline create(@NonNull Pipeline pipeline);

    List<Pipeline> createAll(@NonNull List<Pipeline> newPipelines);

    Pipeline update(@NonNull Pipeline pipeline);

    Page<Pipeline> getAllByStatuses(@NonNull Set<PipelineStatus> statuses, @NonNull Pageable pagination);

    Page<Pipeline> getAll(@NonNull PipelineFilter filter, @NonNull Pageable pagination);

    ExistContainer<Pipeline, Long> existsById(@NonNull Set<Long> pipelineIds);

    void deleteAllById(Set<Long> pipelineIds);

}
