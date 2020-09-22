package org.sadtech.bot.vcs.teamcity.core.service;

import lombok.NonNull;
import org.sadtech.basic.context.service.SimpleManagerService;
import org.sadtech.bot.vcs.teamcity.core.domain.entity.TeamcitySetting;

import java.util.List;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
public interface TeamcitySettingService extends SimpleManagerService<TeamcitySetting, Long> {

    List<TeamcitySetting> getAllByProjectId(@NonNull String projectId);

}
