package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import dev.struchkov.haiti.filter.Filter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author upagge 17.01.2021
 */
public interface PipelineRepository {

    Pipeline save(Pipeline pipeline);

    Optional<Pipeline> findById(Long pipelineId);

    Page<Pipeline> findAllByStatuses(Set<PipelineStatus> statuses, Pageable pagination);

    List<Pipeline> findAllById(Set<Long> pipelineIds);

    void deleteAllByIds(Set<Long> pipelineIds);

    Page<Pipeline> filter(Filter filter, Pageable pagination);

}
