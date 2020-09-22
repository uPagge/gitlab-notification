package org.sadtech.bot.vcs.teamcity.core.repository.jpa;

import org.sadtech.bot.vcs.teamcity.core.domain.entity.TeamcitySetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
public interface TeamcitySettingJpaRepository extends JpaRepository<TeamcitySetting, Long> {

    List<TeamcitySetting> findAllByProjectId(String projectId);

}
