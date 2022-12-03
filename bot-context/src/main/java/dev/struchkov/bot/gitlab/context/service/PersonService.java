package dev.struchkov.bot.gitlab.context.service;

import dev.struchkov.bot.gitlab.context.domain.ExistsContainer;
import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import lombok.NonNull;

import java.util.List;
import java.util.Set;

/**
 * @author upagge 15.01.2021
 */
public interface PersonService {

    Person create(@NonNull Person person);

    Person update(@NonNull Person person);

    Person getByIdOrThrown(@NonNull Long personId);

    ExistsContainer<Person, Long> existsById(Set<Long> personIds);

    List<Person> createAll(List<Person> newPersons);

}
