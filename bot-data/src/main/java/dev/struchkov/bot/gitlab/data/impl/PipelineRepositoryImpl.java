package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import dev.struchkov.bot.gitlab.context.repository.PipelineRepository;
import dev.struchkov.bot.gitlab.data.jpa.PipelineJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public List<Pipeline> findAllByStatuses(Set<PipelineStatus> statuses) {
        return jpaRepository.findAllByStatusIn(statuses);
    }

    @Override
    public List<Pipeline> findAllById(Set<Long> pipelineIds) {
        return jpaRepository.findAllById(pipelineIds);
    }

    @Override
    @Transactional
    public void deleteByCreatedBefore(LocalDateTime date) {
        jpaRepository.deleteAllByCreatedBefore(date);
    }

}
