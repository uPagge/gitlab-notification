package org.sadtech.bot.gitlab.data.jpa;

import org.sadtech.bot.gitlab.context.domain.entity.Discussion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * // TODO: 11.02.2021 Добавить описание.
 *
 * @author upagge 11.02.2021
 */
public interface DiscussionJpaRepository extends JpaRepository<Discussion, String> {

}
