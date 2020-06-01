package org.sadtech.bot.bitbucketbot.service.converter;

import org.sadtech.bot.bitbucketbot.domain.entity.User;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.UserJson;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class UserJsonConverter implements Converter<UserJson, User> {

    @Override
    public User convert(UserJson source) {
        return User.builder()
                .fullName(source.getDisplayName())
                .login(source.getName())
                .build();
    }

}
