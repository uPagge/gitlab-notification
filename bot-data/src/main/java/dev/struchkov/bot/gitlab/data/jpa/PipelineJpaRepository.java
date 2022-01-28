package dev.struchkov.bot.gitlab.data.jpa;

import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.util.Set;

/**
 * @author upagge 17.01.2021
 */
public interface PipelineJpaRepository extends JpaRepositoryImplementation<Pipeline, Long> {

    Page<Pipeline> findAllByStatusIn(Set<PipelineStatus> statuses, Pageable pageable);

}
