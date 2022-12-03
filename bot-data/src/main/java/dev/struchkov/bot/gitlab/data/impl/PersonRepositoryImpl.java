package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.context.repository.PersonRepository;
import dev.struchkov.bot.gitlab.data.jpa.PersonJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author upagge 15.01.2021
 */
@Repository
@RequiredArgsConstructor
public class PersonRepositoryImpl implements PersonRepository {

    private final PersonJpaRepository jpaRepository;

    @Override
    public Person save(Person person) {
        return jpaRepository.save(person);
    }

    @Override
    public Optional<Person> findById(Long personId) {
        return jpaRepository.findById(personId);
    }

    @Override
    public List<Person> findAllById(Set<Long> personIds) {
        return jpaRepository.findAllById(personIds);
    }

}
