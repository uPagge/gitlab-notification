package org.sadtech.bot.vcs.bitbucketbot.data.jpa;

import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.PullRequestMini;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * // TODO: 12.09.2020 Добавить описание.
 *
 * @author upagge 12.09.2020
 */
public interface PullRequestMiniRepositoryJpa extends JpaRepository<PullRequestMini, Long> {

}
