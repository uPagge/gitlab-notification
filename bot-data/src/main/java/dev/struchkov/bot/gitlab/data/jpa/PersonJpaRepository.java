package dev.struchkov.bot.gitlab.data.jpa;

import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * // TODO: 15.01.2021 Добавить описание.
 *
 * @author upagge 15.01.2021
 */
public interface PersonJpaRepository extends JpaRepository<Person, Long> {

}
