package org.sadtech.bot.bitbucketbot.utils;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.entity.User;

import java.util.Collections;
import java.util.Set;

public class NonNullUtils {

    private NonNullUtils() {
        throw new IllegalStateException("Утилитный класс");
    }

    public static Set<Long> telegramIdByUser(@NonNull User user) {
        return user.getTelegramId() != null ? Collections.singleton(user.getTelegramId()) : Collections.emptySet();
    }

}
