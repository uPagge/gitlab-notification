package dev.struchkov.bot.gitlab.telegram.service.notify;


import dev.struchkov.godfather.simple.domain.BoxAnswer;

import java.util.List;
import java.util.stream.Collectors;

public interface NotifyBoxAnswerGenerator<T> {

    BoxAnswer generate(T notify);

    default List<BoxAnswer> generate(List<T> notify) {
        return notify.stream().map(this::generate).collect(Collectors.toList());
    }

    String getNotifyType();

}
