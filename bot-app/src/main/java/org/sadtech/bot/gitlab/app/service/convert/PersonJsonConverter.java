package org.sadtech.bot.gitlab.app.service.convert;

import org.sadtech.bot.gitlab.context.domain.entity.Person;
import org.sadtech.bot.gitlab.sdk.domain.PersonJson;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * // TODO: 15.01.2021 Добавить описание.
 *
 * @author upagge 15.01.2021
 */
@Component
public class PersonJsonConverter implements Converter<PersonJson, Person> {

    @Override
    public Person convert(PersonJson source) {
        final Person person = new Person();
        person.setId(source.getId());
        person.setName(source.getName());
        person.setUserName(source.getUsername());
        person.setWebUrl(source.getWebUrl());
        return person;
    }

}
