package org.sadtech.bot.gitlab.data.jpa;

import org.sadtech.bot.gitlab.context.domain.PipelineStatus;
import org.sadtech.bot.gitlab.context.domain.entity.Pipeline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

/**
 * // TODO: 17.01.2021 Добавить описание.
 *
 * @author upagge 17.01.2021
 */
public interface PipelineJpaRepository extends JpaRepository<Pipeline, Long> {

    Page<Pipeline> findAllByStatusIn(Set<PipelineStatus> statuses, Pageable pageable);

}
