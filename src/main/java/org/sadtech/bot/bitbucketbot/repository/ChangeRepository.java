package org.sadtech.bot.bitbucketbot.repository;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.change.Change;

import java.util.List;

public interface ChangeRepository {

    void add(@NonNull Change change);

    List<Change> getAll();

    void deleteAll(@NonNull List<Change> changes);

}
