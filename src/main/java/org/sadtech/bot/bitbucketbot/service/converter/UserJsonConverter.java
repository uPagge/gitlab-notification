package org.sadtech.bot.bitbucketbot.service.converter;

import org.sadtech.bot.bitbucketbot.domain.entity.Person;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.UserJson;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class UserJsonConverter implements Converter<UserJson, Person> {

    @Override
    public Person convert(UserJson source) {
        final Person person = new Person();
        person.setFullName(source.getDisplayName());
        person.setLogin(source.getName());
        return person;
    }

}
