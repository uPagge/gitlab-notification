package org.sadtech.bot.vcs.teamcity.core.service.impl;

import lombok.NonNull;
import org.sadtech.basic.core.service.AbstractSimpleManagerService;
import org.sadtech.bot.vcs.core.service.NotifyService;
import org.sadtech.bot.vcs.teamcity.core.domain.ServiceNotify;
import org.sadtech.bot.vcs.teamcity.core.domain.entity.BuildShort;
import org.sadtech.bot.vcs.teamcity.core.repository.BuildShortRepository;
import org.sadtech.bot.vcs.teamcity.core.service.BuildShortService;
import org.sadtech.bot.vcs.teamcity.core.service.TeamcitySettingService;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Service
public class BuildShortServiceImpl extends AbstractSimpleManagerService<BuildShort, Long> implements BuildShortService {

    private final TeamcitySettingService teamcitySettingService;
    private final BuildShortRepository buildShortRepository;
    private final NotifyService notifyService;

    public BuildShortServiceImpl(TeamcitySettingService teamcitySettingService, BuildShortRepository buildShortRepository, NotifyService notifyService) {
        super(buildShortRepository);
        this.teamcitySettingService = teamcitySettingService;
        this.buildShortRepository = buildShortRepository;
        this.notifyService = notifyService;
    }

    @Override
    public Set<Long> exists(Set<Long> buildIds) {
        return buildShortRepository.exists(buildIds);
    }

    @Override
    public BuildShort create(@NonNull BuildShort buildShort) {
        final BuildShort newBuildShort = buildShortRepository.save(buildShort);

        teamcitySettingService.getAllByProjectId(buildShort.getProjectId())
                .forEach(
                        teamcitySetting -> notifyService.send(
                                ServiceNotify.builder()
                                        .buildShort(buildShort)
                                        .chatId(teamcitySetting.getChatId())
                                        .build()
                        )
                );

        return newBuildShort;
    }

    @Override
    public BuildShort update(@NonNull BuildShort buildShort) {
        return buildShortRepository.save(buildShort);
    }

}
