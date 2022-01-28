package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.haiti.context.repository.SimpleManagerRepository;

/**
 * @author upagge 15.01.2021
 */
public interface PersonRepository extends SimpleManagerRepository<Person, Long> {

}
