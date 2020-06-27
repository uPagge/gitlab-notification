package org.sadtech.bot.bitbucketbot.service;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.entity.Task;

import java.util.Collection;
import java.util.List;

public interface TaskService {

    Task create(@NonNull Task task);

    void deleteById(@NonNull Long id);

    Task update(@NonNull Task task);

    List<Task> createAll(@NonNull Collection<Task> tasks);

    Long getLastTaskId();

}
