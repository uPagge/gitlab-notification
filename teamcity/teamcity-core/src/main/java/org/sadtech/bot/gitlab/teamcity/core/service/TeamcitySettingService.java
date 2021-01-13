package org.sadtech.bot.gitlab.teamcity.core.service;

import lombok.NonNull;
import org.sadtech.bot.gitlab.teamcity.core.domain.entity.TeamcitySetting;
import org.sadtech.haiti.context.service.SimpleManagerService;

import java.util.List;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
public interface TeamcitySettingService extends SimpleManagerService<TeamcitySetting, Long> {

    List<TeamcitySetting> getAllByProjectId(@NonNull String projectId);

}
