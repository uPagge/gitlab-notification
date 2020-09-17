package org.sadtech.bot.vcs.bitbucket.service.converter;

import org.sadtech.bot.vcs.bitbucket.sdk.domain.UserJson;
import org.sadtech.bot.vcs.core.domain.entity.Person;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserJsonConverter implements Converter<UserJson, Person> {

    @Override
    public Person convert(UserJson source) {
        final Person person = new Person();
        person.setFullName(source.getDisplayName());
        person.setLogin(source.getName());
        return person;
    }

}
