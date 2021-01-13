package org.sadtech.bot.gitlab.teamcity.core.repository;

import lombok.NonNull;
import org.sadtech.bot.gitlab.teamcity.core.domain.entity.TeamcitySetting;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;

import java.util.List;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
public interface TeamcitySettingRepository extends SimpleManagerRepository<TeamcitySetting, Long> {

    List<TeamcitySetting> findAllByProjectId(@NonNull String projectId);

}
