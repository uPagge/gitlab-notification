package org.sadtech.bot.gitlab.teamcity.core.repository.jpa;

import org.sadtech.bot.gitlab.teamcity.core.domain.entity.TeamcityProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
public interface TeamcityProjectJpaRepository extends JpaRepository<TeamcityProject, String> {

    @Query("SELECT t.id FROM TeamcityProject t WHERE t.id IN :projectIds")
    List<String> existsAllById(@Param("projectIds") Set<String> projectIds);

}
