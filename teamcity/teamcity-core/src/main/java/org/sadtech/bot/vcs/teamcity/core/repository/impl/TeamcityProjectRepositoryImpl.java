package org.sadtech.bot.vcs.teamcity.core.repository.impl;

import lombok.NonNull;
import org.sadtech.bot.vcs.teamcity.core.domain.entity.TeamcityProject;
import org.sadtech.bot.vcs.teamcity.core.repository.TeamcityProjectRepository;
import org.sadtech.bot.vcs.teamcity.core.repository.jpa.TeamcityProjectJpaRepository;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Repository
public class TeamcityProjectRepositoryImpl extends AbstractSimpleManagerRepository<TeamcityProject, String> implements TeamcityProjectRepository {

    private final TeamcityProjectJpaRepository teamcityProjectJpaRepository;

    public TeamcityProjectRepositoryImpl(TeamcityProjectJpaRepository teamcityProjectJpaRepository) {
        super(teamcityProjectJpaRepository);
        this.teamcityProjectJpaRepository = teamcityProjectJpaRepository;
    }

    @Override
    public List<String> exists(@NonNull Set<String> projectIds) {
        return teamcityProjectJpaRepository.existsAllById(projectIds);
    }
}
