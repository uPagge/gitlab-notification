package org.sadtech.bot.bitbucketbot.service.converter;

import org.sadtech.bot.bitbucketbot.domain.entity.Person;
import org.sadtech.bot.bitbucketbot.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter implements Converter<UserDto, Person> {

    @Override
    public Person convert(UserDto source) {
        return Person.builder()
                .login(source.getLogin())
                .token(source.getToken())
                .telegramId(source.getTelegramId())
                .build();
    }

}
