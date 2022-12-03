package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.entity.Person;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author upagge 15.01.2021
 */
public interface PersonRepository {

    Person save(Person person);

    Optional<Person> findById(Long personId);

    List<Person> findAllById(Set<Long> personIds);

}
