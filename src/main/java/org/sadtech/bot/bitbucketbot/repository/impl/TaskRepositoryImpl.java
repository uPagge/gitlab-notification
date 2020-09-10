package org.sadtech.bot.bitbucketbot.repository.impl;

import org.sadtech.basic.database.repository.manager.AbstractSimpleManagerRepository;
import org.sadtech.bot.bitbucketbot.domain.entity.Task;
import org.sadtech.bot.bitbucketbot.repository.TaskRepository;
import org.sadtech.bot.bitbucketbot.repository.jpa.TaskRepositoryJpa;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class TaskRepositoryImpl extends AbstractSimpleManagerRepository<Task, Long> implements TaskRepository {

    private final TaskRepositoryJpa taskRepositoryJpa;

    public TaskRepositoryImpl(TaskRepositoryJpa taskRepositoryJpa) {
        super(taskRepositoryJpa);
        this.taskRepositoryJpa = taskRepositoryJpa;
    }

    @Override
    public Optional<Task> findFirstByOrderByIdDesc() {
        return taskRepositoryJpa.findFirstByOrderByIdDesc();
    }

}
