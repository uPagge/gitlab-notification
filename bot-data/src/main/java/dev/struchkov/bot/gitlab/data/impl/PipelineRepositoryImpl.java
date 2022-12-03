package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import dev.struchkov.bot.gitlab.context.repository.PipelineRepository;
import dev.struchkov.bot.gitlab.data.jpa.PipelineJpaRepository;
import dev.struchkov.haiti.filter.Filter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author upagge 17.01.2021
 */
@Repository
@RequiredArgsConstructor
public class PipelineRepositoryImpl implements PipelineRepository {

    private final PipelineJpaRepository jpaRepository;

    @Override
    public Pipeline save(Pipeline pipeline) {
        return jpaRepository.save(pipeline);
    }

    @Override
    public Optional<Pipeline> findById(Long pipelineId) {
        return jpaRepository.findById(pipelineId);
    }

    @Override
    public Page<Pipeline> findAllByStatuses(Set<PipelineStatus> statuses, Pageable pagination) {
        return jpaRepository.findAllByStatusIn(statuses, pagination);
    }

    @Override
    public List<Pipeline> findAllById(Set<Long> pipelineIds) {
        return jpaRepository.findAllById(pipelineIds);
    }

    @Override
    public void deleteAllByIds(Set<Long> pipelineIds) {
        jpaRepository.deleteAllById(pipelineIds);
    }

    @Override
    public Page<Pipeline> filter(Filter filter, Pageable pagination) {
        return jpaRepository.findAll(filter.<Specification<Pipeline>>build(), pagination);
    }

}
