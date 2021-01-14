package org.sadtech.bot.gitlab.teamcity.core.service.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.teamcity.core.domain.entity.TeamcitySetting;
import org.sadtech.bot.gitlab.teamcity.core.repository.TeamcitySettingRepository;
import org.sadtech.bot.gitlab.teamcity.core.service.TeamcitySettingService;
import org.sadtech.haiti.context.domain.ExistsContainer;
import org.sadtech.haiti.core.service.AbstractSimpleManagerService;

import java.util.Collection;
import java.util.List;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
//@Service
public class TeamcitySettingServiceImpl extends AbstractSimpleManagerService<TeamcitySetting, Long> implements TeamcitySettingService {

    private final TeamcitySettingRepository teamcitySettingRepository;

    public TeamcitySettingServiceImpl(TeamcitySettingRepository teamcitySettingRepository) {
        super(teamcitySettingRepository);
        this.teamcitySettingRepository = teamcitySettingRepository;
    }

    @Override
    public List<TeamcitySetting> getAllByProjectId(@NonNull String projectId) {
        return teamcitySettingRepository.findAllByProjectId(projectId);
    }

    @Override
    public TeamcitySetting create(@NonNull TeamcitySetting entity) {
        return null;
    }

    @Override
    public TeamcitySetting update(@NonNull TeamcitySetting entity) {
        return null;
    }

    @Override
    public ExistsContainer<TeamcitySetting, Long> existsById(@NonNull Collection<Long> collection) {
        return null;
    }

}
