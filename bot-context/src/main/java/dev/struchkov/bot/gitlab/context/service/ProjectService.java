package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author upagge 14.01.2021
 */
public interface ProjectService {

    Project create(@NonNull Project project, boolean sendNotify);

    Project update(@NonNull Project project);

    Project getByIdOrThrow(@NonNull Long projectId);

    List<Project> createAll(List<Project> newProjects);

    boolean existsById(Long projectId);

    ExistContainer<Project, Long> existsById(Set<Long> projectIds);

    Set<Long> getAllIdByProcessingEnable();

    Optional<String> getProjectNameById(Long projectId);

    Set<Long> getAllIds();

    void notification(boolean enable, Set<Long> projectIds);

    void processing(boolean enable, Set<Long> projectIds);

}
