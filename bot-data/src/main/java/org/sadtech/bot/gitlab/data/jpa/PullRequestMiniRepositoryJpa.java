package org.sadtech.bot.gitlab.data.jpa;

import org.sadtech.bot.gitlab.context.domain.entity.PullRequestMini;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * // TODO: 12.09.2020 Добавить описание.
 *
 * @author upagge 12.09.2020
 */

@NoRepositoryBean
public interface PullRequestMiniRepositoryJpa extends JpaRepository<PullRequestMini, Long> {

}
