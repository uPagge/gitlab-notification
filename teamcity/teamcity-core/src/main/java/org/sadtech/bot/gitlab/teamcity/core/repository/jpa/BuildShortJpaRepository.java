package org.sadtech.bot.gitlab.teamcity.core.repository.jpa;

import org.sadtech.bot.gitlab.teamcity.core.domain.entity.BuildShort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
public interface BuildShortJpaRepository extends JpaRepository<BuildShort, Long> {

    @Query("SELECT b.id FROM BuildShort b WHERE b.id IN :buildIds")
    Set<Long> existsByIds(@Param("buildIds") Set<Long> buildIds);

}
