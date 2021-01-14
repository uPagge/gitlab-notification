package org.sadtech.bot.vcs.bitbucket.core.service.converter;

import org.sadtech.bot.gitlab.sdk.domain.UserJson;
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
