package com.tsc.bitbucketbot.repository;

import com.tsc.bitbucketbot.domain.change.Change;
import lombok.NonNull;

import java.util.List;

public interface ChangeRepository {

    void add(@NonNull Change change);

    List<Change> getAll();

    void deleteAll(@NonNull List<Change> changes);

}
