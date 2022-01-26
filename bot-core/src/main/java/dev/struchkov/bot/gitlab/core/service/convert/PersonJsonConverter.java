package dev.struchkov.bot.gitlab.core.service.convert;

import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.sdk.domain.PersonJson;
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
