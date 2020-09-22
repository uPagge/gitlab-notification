package org.sadtech.bot.vcs.teamcity.core.repository;

import lombok.NonNull;
import org.sadtech.basic.context.repository.SimpleManagerRepository;
import org.sadtech.bot.vcs.teamcity.core.domain.entity.TeamcitySetting;

import java.util.List;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
public interface TeamcitySettingRepository extends SimpleManagerRepository<TeamcitySetting, Long> {

    List<TeamcitySetting> findAllByProjectId(@NonNull String projectId);

}
