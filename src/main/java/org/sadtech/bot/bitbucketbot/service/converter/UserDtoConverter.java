package org.sadtech.bot.bitbucketbot.service.converter;

import org.sadtech.bot.bitbucketbot.domain.entity.Person;
import org.sadtech.bot.bitbucketbot.dto.UserDto;
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
