package org.sadtech.bot.gitlab.teamcity.core.service.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.service.NotifyService;
import org.sadtech.bot.gitlab.teamcity.core.domain.TeamcityBuildNotify;
import org.sadtech.bot.gitlab.teamcity.core.domain.entity.BuildShort;
import org.sadtech.bot.gitlab.teamcity.core.domain.entity.TeamcitySetting;
import org.sadtech.bot.gitlab.teamcity.core.repository.BuildShortRepository;
import org.sadtech.bot.gitlab.teamcity.core.service.BuildShortService;
import org.sadtech.bot.gitlab.teamcity.core.service.TeamcitySettingService;
import org.sadtech.bot.gitlab.teamcity.sdk.BuildStatus;
import org.sadtech.haiti.context.domain.ExistsContainer;
import org.sadtech.haiti.core.service.AbstractSimpleManagerService;

import java.util.Collection;
import java.util.Set;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
//@Service
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
                .forEach(teamcitySetting -> sendNotification(teamcitySetting, buildShort));

        return newBuildShort;
    }

    private void sendNotification(TeamcitySetting teamcitySetting, BuildShort buildShort) {
        if (isStatusBuild(teamcitySetting, buildShort.getStatus()) && isTypeBuild(teamcitySetting, buildShort.getBuildTypeId())) {
            notifyService.send(
                    TeamcityBuildNotify.builder()
                            .buildShort(buildShort)
                            .build()
            );
        }
    }

    private boolean isTypeBuild(TeamcitySetting teamcitySetting, String buildTypeId) {
        return teamcitySetting.getBuildTypeId() == null
                || (teamcitySetting.getBuildTypeId().equals(buildTypeId));
    }

    private boolean isStatusBuild(TeamcitySetting teamcitySetting, BuildStatus buildStatus) {
        return (teamcitySetting.isFailure() && BuildStatus.FAILURE.equals(buildStatus))
                || (teamcitySetting.isSuccess() && BuildStatus.SUCCESS.equals(buildStatus));
    }

    @Override
    public BuildShort update(@NonNull BuildShort buildShort) {
        return buildShortRepository.save(buildShort);
    }

    @Override
    public ExistsContainer<BuildShort, Long> existsById(@NonNull Collection<Long> collection) {
        return null;
    }

}
