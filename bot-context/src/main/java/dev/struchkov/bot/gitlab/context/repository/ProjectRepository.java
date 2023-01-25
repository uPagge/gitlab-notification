package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author upagge 14.01.2021
 */
public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findById(Long projectId);

    List<Project> findAllById(Set<Long> projectIds);

    boolean existById(Long projectId);

    Page<Project> findAllById(Pageable pagination);

    Set<Long> findAllIdByProcessingEnable();

    Optional<String> findProjectNameById(Long projectId);

    Set<Long> findAllIds();

    void notification(boolean enable, Set<Long> projectIds);

    void processing(boolean enable, Set<Long> projectIds);

}
