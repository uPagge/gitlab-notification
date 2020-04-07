package com.tsc.bitbucketbot.repository.impl;

import com.tsc.bitbucketbot.domain.change.Change;
import com.tsc.bitbucketbot.repository.ChangeRepository;
import lombok.NonNull;
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
