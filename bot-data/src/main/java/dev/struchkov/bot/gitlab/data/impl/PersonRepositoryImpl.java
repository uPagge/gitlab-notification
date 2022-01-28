package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.context.repository.PersonRepository;
import dev.struchkov.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author upagge 15.01.2021
 */
@Repository
public class PersonRepositoryImpl extends AbstractSimpleManagerRepository<Person, Long> implements PersonRepository {

    public PersonRepositoryImpl(JpaRepository<Person, Long> jpaRepository) {
        super(jpaRepository);
    }

}
