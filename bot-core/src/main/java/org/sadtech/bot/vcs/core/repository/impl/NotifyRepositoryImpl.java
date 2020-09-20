package org.sadtech.bot.vcs.core.repository.impl;

import lombok.NonNull;
import org.sadtech.bot.vcs.core.domain.notify.Notify;
import org.sadtech.bot.vcs.core.repository.NotifyRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class NotifyRepositoryImpl implements NotifyRepository {

    private final List<Notify> list = new ArrayList<>();
    private long count = 0;

    @Override
    public <T extends Notify> T add(@NonNull T notify) {
        notify.setId(count++);
        list.add(notify);
        return notify;
    }

    @Override
    public List<Notify> getAll() {
        return new ArrayList<>(list);
    }

    @Override
    public void deleteAll(@NonNull List<Notify> notifies) {
        list.removeAll(notifies);
    }


}
