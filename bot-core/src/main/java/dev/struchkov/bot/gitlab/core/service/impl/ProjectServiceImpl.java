package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.ExistsContainer;
import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import dev.struchkov.bot.gitlab.context.domain.notify.NewProjectNotify;
import dev.struchkov.bot.gitlab.context.repository.ProjectRepository;
import dev.struchkov.bot.gitlab.context.service.NotifyService;
import dev.struchkov.bot.gitlab.context.service.PersonService;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.context.exception.NotFoundException.notFoundException;

/**
 * @author upagge 14.01.2021
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository repository;

    private final NotifyService notifyService;
    private final PersonService personService;
    private final PersonInformation personInformation;

    @Override
    public Project create(@NonNull Project project) {
        final Project newProject = repository.save(project);

        if (!personInformation.getId().equals(newProject.getCreatorId())) {
            final String authorName = personService.getByIdOrThrown(newProject.getCreatorId()).getName();
            sendNotifyNewProject(newProject, authorName);
        }

        return newProject;
    }

    @Override
    public Project update(@NonNull Project project) {
        return repository.save(project);
    }

    @Override
    public Project getByIdOrThrow(@NonNull Long projectId) {
        return repository.findById(projectId)
                .orElseThrow(notFoundException("Проект не найден"));
    }

    @Override
    public Page<Project> getAll(@NonNull Pageable pagination) {
        return repository.findAllById(pagination);
    }

    @Override
    public List<Project> createAll(List<Project> newProjects) {
        return newProjects.stream()
                .map(this::create)
                .toList();
    }

    @Override
    public boolean existsById(Long projectId) {
        return repository.existById(projectId);
    }

    @Override
    public ExistsContainer<Project, Long> existsById(Set<Long> projectIds) {
        final List<Project> existsEntity = repository.findAllById(projectIds);
        final Set<Long> existsIds = existsEntity.stream().map(Project::getId).collect(Collectors.toSet());
        if (existsIds.containsAll(projectIds)) {
            return ExistsContainer.allFind(existsEntity);
        } else {
            final Set<Long> noExistsId = projectIds.stream()
                    .filter(id -> !existsIds.contains(id))
                    .collect(Collectors.toSet());
            return ExistsContainer.notAllFind(existsEntity, noExistsId);
        }
    }

    private void sendNotifyNewProject(Project newProject, String authorName) {
        notifyService.send(
                NewProjectNotify.builder()
                        .projectDescription(newProject.getDescription())
                        .projectName(newProject.getName())
                        .projectUrl(newProject.getWebUrl())
                        .authorName(authorName)
                        .build()
        );
    }

}
