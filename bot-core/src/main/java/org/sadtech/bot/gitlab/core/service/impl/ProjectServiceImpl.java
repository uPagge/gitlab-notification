package org.sadtech.bot.gitlab.core.service.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Project;
import org.sadtech.bot.gitlab.context.repository.ProjectRepository;
import org.sadtech.bot.gitlab.context.service.ProjectService;
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

    public ProjectServiceImpl(SimpleManagerRepository<Project, Long> repository, ProjectRepository projectRepository) {
        super(repository);
        this.projectRepository = projectRepository;
    }

    @Override
    public Project create(@NonNull Project project) {
        return projectRepository.save(project);
    }

    @Override
    public Project update(@NonNull Project project) {
        return projectRepository.save(project);
    }

}
