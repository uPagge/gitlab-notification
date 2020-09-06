package org.sadtech.bot.bitbucketbot.repository.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.domain.entity.Task;
import org.sadtech.bot.bitbucketbot.repository.TaskRepository;
import org.sadtech.bot.bitbucketbot.repository.jpa.TaskRepositoryJpa;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepository {

    private final TaskRepositoryJpa taskRepositoryJpa;

    @Override
    public Task save(@NonNull Task task) {
        return taskRepositoryJpa.save(task);
    }

    @Override
    public void deleteById(@NonNull Long id) {
        taskRepositoryJpa.deleteById(id);
    }

    @Override
    public Optional<Task> findById(@NonNull Long id) {
        return taskRepositoryJpa.findById(id);
    }

    @Override
    public Optional<Task> findFirstByOrderByIdDesc() {
        return Optional.empty();
    }

}
