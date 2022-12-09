package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.context.repository.PersonRepository;
import dev.struchkov.bot.gitlab.context.service.PersonService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.context.exception.NotFoundException.notFoundException;

/**
 * @author upagge 15.01.2021
 */
@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

    private final PersonRepository repository;

    @Override
    public Person create(@NonNull Person person) {
        return repository.save(person);
    }

    @Override
    public Person update(@NonNull Person person) {
        return repository.save(person);
    }

    @Override
    public Person getByIdOrThrown(@NonNull Long personId) {
        return repository.findById(personId)
                .orElseThrow(notFoundException("Пользователь не найден"));
    }

    @Override
    public ExistContainer<Person, Long> existsById(Set<Long> personIds) {
        final List<Person> existsEntity = repository.findAllById(personIds);
        final Set<Long> existsIds = existsEntity.stream().map(Person::getId).collect(Collectors.toSet());
        if (existsIds.containsAll(personIds)) {
            return ExistContainer.allFind(existsEntity);
        } else {
            final Set<Long> noExistsId = personIds.stream()
                    .filter(id -> !existsIds.contains(id))
                    .collect(Collectors.toSet());
            return ExistContainer.notAllFind(existsEntity, noExistsId);
        }
    }

    @Override
    public List<Person> createAll(List<Person> newPersons) {
        return newPersons.stream()
                .map(this::create)
                .toList();
    }

}
