package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import dev.struchkov.bot.gitlab.context.domain.notify.project.NewProjectNotify;
import dev.struchkov.bot.gitlab.context.repository.ProjectRepository;
import dev.struchkov.bot.gitlab.context.service.NotifyService;
import dev.struchkov.bot.gitlab.context.service.PersonService;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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

    @Override
    @Transactional
    public Project create(@NonNull Project project, boolean sendNotify) {
        final Project newProject = repository.save(project);

        if (sendNotify) {
            final String authorName = personService.getByIdOrThrown(newProject.getCreatorId()).getName();
            notifyAboutNewProject(newProject, authorName);
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
    @Transactional
    public List<Project> createAll(List<Project> newProjects) {
        return newProjects.stream()
                .map(newProject -> create(newProject, true))
                .toList();
    }

    @Override
    public boolean existsById(Long projectId) {
        return repository.existById(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public ExistContainer<Project, Long> existsById(Set<Long> projectIds) {
        final List<Project> existsEntity = repository.findAllById(projectIds);
        final Set<Long> existsIds = existsEntity.stream().map(Project::getId).collect(Collectors.toSet());
        if (existsIds.containsAll(projectIds)) {
            return ExistContainer.allFind(existsEntity);
        } else {
            final Set<Long> noExistsId = projectIds.stream()
                    .filter(id -> !existsIds.contains(id))
                    .collect(Collectors.toSet());
            return ExistContainer.notAllFind(existsEntity, noExistsId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> getAllIdByProcessingEnable() {
        return repository.findAllIdByProcessingEnable();
    }

    @Override
    public Optional<String> getProjectNameById(@NonNull Long projectId) {
        return repository.findProjectNameById(projectId);
    }

    @Override
    public Set<Long> getAllIds() {
        return repository.findAllIds();
    }

    @Override
    @Transactional
    public void notification(boolean enable, @NonNull Set<Long> projectIds) {
        repository.notification(enable, projectIds);
    }

    @Override
    @Transactional
    public void processing(boolean enable, @NonNull Set<Long> projectIds) {
        repository.processing(enable, projectIds);
    }

    private void notifyAboutNewProject(Project newProject, String authorName) {
        notifyService.send(
                NewProjectNotify.builder()
                        .projectId(newProject.getId())
                        .projectDescription(newProject.getDescription())
                        .projectName(newProject.getName())
                        .projectUrl(newProject.getWebUrl())
                        .sshUrlToRepo(newProject.getSshUrlToRepo())
                        .httpUrlToRepo(newProject.getHttpUrlToRepo())
                        .authorName(authorName)
                        .build()
        );
    }

}
