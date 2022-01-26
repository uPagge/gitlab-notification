package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.context.repository.PersonRepository;
import dev.struchkov.bot.gitlab.context.service.PersonService;
import dev.struchkov.haiti.core.service.AbstractSimpleManagerService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

/**
 * // TODO: 15.01.2021 Добавить описание.
 *
 * @author upagge 15.01.2021
 */
@Service
public class PersonServiceImpl extends AbstractSimpleManagerService<Person, Long> implements PersonService {

    private final PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        super(personRepository);
        this.personRepository = personRepository;
    }

    @Override
    public Person create(@NonNull Person person) {
        return personRepository.save(person);
    }

    @Override
    public Person update(@NonNull Person person) {
        return personRepository.save(person);
    }

}
