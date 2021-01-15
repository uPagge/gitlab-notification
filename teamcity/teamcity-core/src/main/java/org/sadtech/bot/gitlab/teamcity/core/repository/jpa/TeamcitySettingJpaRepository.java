package org.sadtech.bot.gitlab.teamcity.core.repository.jpa;

import org.sadtech.bot.gitlab.teamcity.core.domain.entity.TeamcitySetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@NoRepositoryBean
public interface TeamcitySettingJpaRepository extends JpaRepository<TeamcitySetting, Long> {

    List<TeamcitySetting> findAllByProjectId(String projectId);

}
