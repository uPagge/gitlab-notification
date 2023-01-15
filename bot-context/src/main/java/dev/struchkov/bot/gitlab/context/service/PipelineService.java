package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import lombok.NonNull;

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

    List<Pipeline> updateAll(@NonNull List<Pipeline> pipelines);

    List<Pipeline> getAllByStatuses(@NonNull Set<PipelineStatus> statuses);

    ExistContainer<Pipeline, Long> existsById(@NonNull Set<Long> pipelineIds);

    void cleanOld();

    Set<Long> getAllIds();

}
