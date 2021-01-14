package org.sadtech.bot.gitlab.app.service.executor;

import lombok.NonNull;

import java.util.List;

public interface Executor<T, D> {

    boolean registration(@NonNull List<T> seeker);

    List<D> getResult();

}
