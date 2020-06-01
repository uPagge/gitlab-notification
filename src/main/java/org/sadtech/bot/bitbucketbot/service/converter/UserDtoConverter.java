package org.sadtech.bot.bitbucketbot.service.converter;

import org.sadtech.bot.bitbucketbot.domain.entity.User;
import org.sadtech.bot.bitbucketbot.dto.UserDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserDtoConverter implements Converter<UserDto, User> {

    @Override
    public User convert(UserDto source) {
        return User.builder()
                .login(source.getLogin())
                .token(source.getToken())
                .telegramId(source.getTelegramId())
                .build();
    }

}
