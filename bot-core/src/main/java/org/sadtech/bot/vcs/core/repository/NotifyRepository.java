package org.sadtech.bot.vcs.core.repository;

import lombok.NonNull;
import org.sadtech.bot.vcs.core.domain.notify.Notify;

import java.util.List;

public interface NotifyRepository {

    <T extends Notify> T add(@NonNull T notify);

    List<Notify> getAll();

    void deleteAll(@NonNull List<Notify> notifies);

}
