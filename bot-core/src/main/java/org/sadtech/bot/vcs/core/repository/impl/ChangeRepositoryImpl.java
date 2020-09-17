package org.sadtech.bot.vcs.core.repository.impl;

import lombok.NonNull;
import org.sadtech.bot.vcs.core.domain.change.Change;
import org.sadtech.bot.vcs.core.repository.ChangeRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ChangeRepositoryImpl implements ChangeRepository {

    private final List<Change> list = new ArrayList<>();
    private long count = 0;

    @Override
    public <T extends Change> T add(@NonNull T change) {
        change.setId(count++);
        list.add(change);
        return change;
    }

    @Override
    public List<Change> getAll() {
        return new ArrayList<>(list);
    }

    @Override
    public void deleteAll(@NonNull List<Change> changes) {
        list.removeAll(changes);
    }


}
