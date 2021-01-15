package org.sadtech.bot.gitlab.core.service.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.PersonInformation;
import org.sadtech.bot.gitlab.context.domain.entity.Project;
import org.sadtech.bot.gitlab.context.domain.notify.NewProjectNotify;
import org.sadtech.bot.gitlab.context.repository.ProjectRepository;
import org.sadtech.bot.gitlab.context.service.NotifyService;
import org.sadtech.bot.gitlab.context.service.PersonService;
import org.sadtech.bot.gitlab.context.service.ProjectService;
import org.sadtech.haiti.context.exception.NotFoundException;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;
import org.sadtech.haiti.core.service.AbstractSimpleManagerService;
import org.springframework.stereotype.Service;

/**
 * // TODO: 14.01.2021 Добавить описание.
 *
 * @author upagge 14.01.2021
 */
@Service
public class ProjectServiceImpl extends AbstractSimpleManagerService<Project, Long> implements ProjectService {

    private final ProjectRepository projectRepository;
    private final NotifyService notifyService;
    private final PersonService personService;
    private final PersonInformation personInformation;

    public ProjectServiceImpl(
            SimpleManagerRepository<Project, Long> repository,
            ProjectRepository projectRepository,
            NotifyService notifyService,
            PersonService personService, PersonInformation personInformation
    ) {
        super(repository);
        this.projectRepository = projectRepository;
        this.notifyService = notifyService;
        this.personService = personService;
        this.personInformation = personInformation;
    }

    @Override
    public Project create(@NonNull Project project) {
        final Project newProject = projectRepository.save(project);

        if (!newProject.getCreatorId().equals(personInformation.getId())) {
            final String authorName = personService.getById(newProject.getCreatorId())
                    .orElseThrow(() -> new NotFoundException("Пользователь не найден"))
                    .getName();
            notifyService.send(
                    NewProjectNotify.builder()
                            .projectDescription(newProject.getDescription())
                            .projectName(newProject.getName())
                            .projectUrl(newProject.getWebUrl())
                            .authorName(authorName)
                            .build()
            );
        }

        return newProject;
    }

    @Override
    public Project update(@NonNull Project project) {
        return projectRepository.save(project);
    }

}
