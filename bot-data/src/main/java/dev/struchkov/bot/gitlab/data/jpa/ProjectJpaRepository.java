package dev.struchkov.bot.gitlab.data.jpa;

import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * // TODO: 14.01.2021 Добавить описание.
 *
 * @author upagge 14.01.2021
 */
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {

}
