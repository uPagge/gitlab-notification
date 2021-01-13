package org.sadtech.bot.gitlab.context.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Answer {

    private final String authorName;
    private final String message;

    public static Answer of(@NonNull String name, @NonNull String message) {
        return new Answer(name, message);
    }

}
