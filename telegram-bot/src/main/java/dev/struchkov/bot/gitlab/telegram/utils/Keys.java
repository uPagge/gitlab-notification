package dev.struchkov.bot.gitlab.telegram.utils;

import dev.struchkov.godfather.main.domain.ContextKey;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Keys {

    public static final ContextKey<Boolean> INIT_SETTING_FINISH = ContextKey.of("INIT_SETTING_FINISH", Boolean.class);
    public static final ContextKey<Integer> INIT_SETTING_PRIVATE_PROJECT_MESSAGE_ID = ContextKey.of("INIT_SETTING_PRIVATE_PROJECT_MESSAGE_ID", Integer.class);
    public static final ContextKey<Integer> INIT_SETTING_PUBLIC_PROJECT_MESSAGE_ID = ContextKey.of("INIT_SETTING_PUBLIC_PROJECT_MESSAGE_ID", Integer.class);

}
