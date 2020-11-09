package org.sadtech.bot.vcs.teamcity.core.service.impl;

import lombok.NonNull;
import org.sadtech.bot.vcs.teamcity.core.domain.entity.TeamcitySetting;
import org.sadtech.bot.vcs.teamcity.core.repository.TeamcitySettingRepository;
import org.sadtech.bot.vcs.teamcity.core.service.TeamcitySettingService;
import org.sadtech.haiti.core.service.AbstractSimpleManagerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Service
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

}
