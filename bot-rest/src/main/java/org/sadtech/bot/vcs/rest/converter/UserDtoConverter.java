package org.sadtech.bot.vcs.rest.converter;

import org.sadtech.bot.vcs.rest.dto.UserDto;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Person;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter implements Converter<UserDto, Person> {

    @Override
    public Person convert(UserDto source) {
        final Person person = new Person();
        person.setLogin(source.getLogin());
        person.setToken(source.getToken());
        person.setTelegramId(source.getTelegramId());
        return person;
    }

}
