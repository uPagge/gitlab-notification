package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.ExistsContainer;
import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

/**
 * @author upagge 14.01.2021
 */
public interface ProjectService {

    Project create(@NonNull Project project);

    Project update(@NonNull Project project);

    Project getByIdOrThrow(@NonNull Long projectId);

    Page<Project> getAll(@NonNull Pageable pagination);

    List<Project> createAll(List<Project> newProjects);

    boolean existsById(Long projectId);

    ExistsContainer<Project, Long> existsById(Set<Long> projectIds);

}
