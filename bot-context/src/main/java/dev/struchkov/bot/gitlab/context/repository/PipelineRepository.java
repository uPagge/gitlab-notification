package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author upagge 17.01.2021
 */
public interface PipelineRepository {

    Pipeline save(Pipeline pipeline);

    Optional<Pipeline> findById(Long pipelineId);

    List<Pipeline> findAllByStatuses(Set<PipelineStatus> statuses);

    List<Pipeline> findAllById(Set<Long> pipelineIds);

    void deleteByCreatedBefore(LocalDateTime date);

    Set<Long> findAllIds();

}
