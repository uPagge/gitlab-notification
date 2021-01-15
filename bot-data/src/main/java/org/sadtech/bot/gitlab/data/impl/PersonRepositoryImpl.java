package org.sadtech.bot.gitlab.data.impl;

import org.sadtech.bot.gitlab.context.domain.entity.Person;
import org.sadtech.bot.gitlab.context.repository.PersonRepository;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * // TODO: 15.01.2021 Добавить описание.
 *
 * @author upagge 15.01.2021
 */
@Repository
public class PersonRepositoryImpl extends AbstractSimpleManagerRepository<Person, Long> implements PersonRepository {

    public PersonRepositoryImpl(JpaRepository<Person, Long> jpaRepository) {
        super(jpaRepository);
    }

}
