package org.sadtech.bot.gitlab.teamcity.core.repository.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.teamcity.core.domain.entity.TeamcitySetting;
import org.sadtech.bot.gitlab.teamcity.core.repository.TeamcitySettingRepository;
import org.sadtech.bot.gitlab.teamcity.core.repository.jpa.TeamcitySettingJpaRepository;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Repository
public class TeamcitySettingRepositoryImpl extends AbstractSimpleManagerRepository<TeamcitySetting, Long> implements TeamcitySettingRepository {

    private final TeamcitySettingJpaRepository jpaRepository;

    public TeamcitySettingRepositoryImpl(TeamcitySettingJpaRepository jpaRepository) {
        super(jpaRepository);
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<TeamcitySetting> findAllByProjectId(@NonNull String projectId) {
        return jpaRepository.findAllByProjectId(projectId);
    }

}
