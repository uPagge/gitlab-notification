package dev.struchkov.bot.gitlab.core.service.convert;

import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import dev.struchkov.bot.gitlab.sdk.domain.ProjectJson;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * @author upagge 14.01.2021
 */
@Component
public class ProjectJsonConverter implements Converter<ProjectJson, Project> {

    @Override
    public Project convert(ProjectJson source) {
        final Project project = new Project();
        project.setId(source.getId());
        project.setCreatedDate(source.getCreatedDate());
        project.setCreatorId(source.getCreatorId());
        project.setDescription(source.getDescription());
        project.setName(source.getName());
        project.setWebUrl(source.getWebUrl());
        return project;
    }

}
