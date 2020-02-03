package com.tsc.bitbucketbot.service.converter;

import com.tsc.bitbucketbot.bitbucket.UserJson;
import com.tsc.bitbucketbot.domain.entity.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [02.02.2020]
 */
@Service
public class UserJsonConverter implements Converter<UserJson, User> {

    @Override
    public User convert(UserJson source) {
        return User.builder()
                .name(source.getDisplayName())
                .login(source.getName())
                .build();
    }

}
