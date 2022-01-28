package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import dev.struchkov.bot.gitlab.context.domain.notify.NewProjectNotify;
import dev.struchkov.bot.gitlab.context.repository.ProjectRepository;
import dev.struchkov.bot.gitlab.context.service.NotifyService;
import dev.struchkov.bot.gitlab.context.service.PersonService;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import dev.struchkov.haiti.context.exception.NotFoundException;
import dev.struchkov.haiti.context.repository.SimpleManagerRepository;
import dev.struchkov.haiti.core.service.AbstractSimpleManagerService;
import lombok.NonNull;
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
            PersonService personService,
            PersonInformation personInformation
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
                    .orElseThrow(NotFoundException.supplier("Пользователь не найден"))
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
