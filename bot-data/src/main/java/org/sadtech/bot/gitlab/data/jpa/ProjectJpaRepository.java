package org.sadtech.bot.gitlab.data.jpa;

import org.sadtech.bot.gitlab.context.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * // TODO: 14.01.2021 Добавить описание.
 *
 * @author upagge 14.01.2021
 */
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {

}
