package dev.struchkov.bot.gitlab.data.jpa;

import dev.struchkov.bot.gitlab.context.domain.PipelineStatus;
import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author upagge 17.01.2021
 */
public interface PipelineJpaRepository extends JpaRepositoryImplementation<Pipeline, Long> {

    List<Pipeline> findAllByStatusIn(Set<PipelineStatus> statuses);

    void deleteAllByCreatedBefore(LocalDateTime date);

    @Query("SELECT p.id FROM Pipeline p")
    Set<Long> findAllIds();

}
