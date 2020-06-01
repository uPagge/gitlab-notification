package org.sadtech.bot.bitbucketbot.repository.impl;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.change.Change;
import org.sadtech.bot.bitbucketbot.repository.ChangeRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ChangeRepositoryImpl implements ChangeRepository {

    private List<Change> list = new ArrayList<>();
    private long count = 0;

    @Override
    public void add(@NonNull Change change) {
        change.setId(count++);
        list.add(change);
    }

    @Override
    public List<Change> getAll() {
        final ArrayList<Change> changes = new ArrayList<>(list);
        return changes;
    }

    @Override
    public void deleteAll(@NonNull List<Change> changes) {
        list.removeAll(changes);
    }


}
