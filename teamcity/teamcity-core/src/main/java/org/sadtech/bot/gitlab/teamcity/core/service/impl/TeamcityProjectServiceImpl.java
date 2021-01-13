package org.sadtech.bot.gitlab.teamcity.core.service.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.teamcity.core.domain.entity.TeamcityProject;
import org.sadtech.bot.gitlab.teamcity.core.repository.TeamcityProjectRepository;
import org.sadtech.bot.gitlab.teamcity.core.service.TeamcityProjectService;
import org.sadtech.haiti.core.service.AbstractSimpleManagerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Service
public class TeamcityProjectServiceImpl extends AbstractSimpleManagerService<TeamcityProject, String> implements TeamcityProjectService {

    private final TeamcityProjectRepository teamcityProjectRepository;

    public TeamcityProjectServiceImpl(TeamcityProjectRepository teamcityProjectRepository) {
        super(teamcityProjectRepository);
        this.teamcityProjectRepository = teamcityProjectRepository;
    }

    @Override
    public List<String> exists(@NonNull Set<String> projectIds) {
        return teamcityProjectRepository.exists(projectIds);
    }

    @Override
    public TeamcityProject create(@NonNull TeamcityProject teamcityProject) {
        return teamcityProjectRepository.save(teamcityProject);
    }

    @Override
    public TeamcityProject update(@NonNull TeamcityProject teamcityProject) {
        return teamcityProjectRepository.save(teamcityProject);
    }

}
