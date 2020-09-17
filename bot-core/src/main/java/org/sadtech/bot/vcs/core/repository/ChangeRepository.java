package org.sadtech.bot.vcs.core.repository;

import lombok.NonNull;
import org.sadtech.bot.vcs.core.domain.change.Change;

import java.util.List;

public interface ChangeRepository {

    <T extends Change> T add(@NonNull T change);

    List<Change> getAll();

    void deleteAll(@NonNull List<Change> changes);

}
