package org.sadtech.bot.bitbucketbot.repository;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.entity.Task;

import java.util.Optional;

public interface TaskRepository {

    Task save(@NonNull Task task);

    void deleteById(@NonNull Long id);

    Optional<Task> findById(@NonNull Long id);

    Optional<Task> findFirstByOrderByIdDesc();

}
