package org.sadtech.bot.vcs.core.repository.impl;

import lombok.NonNull;
import org.sadtech.basic.database.repository.manager.AbstractSimpleManagerRepository;
import org.sadtech.bot.vcs.core.domain.TaskStatus;
import org.sadtech.bot.vcs.core.domain.entity.Task;
import org.sadtech.bot.vcs.core.repository.TaskRepository;
import org.sadtech.bot.vcs.core.repository.jpa.TaskRepositoryJpa;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
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

    @Override
    public List<Task> findByCreateDateBetween(LocalDateTime dateFrom, LocalDateTime dateTo) {
        return taskRepositoryJpa.findByCreateDateBetween(dateFrom, dateTo);
    }

    @Override
    public List<Task> findAllByResponsibleAndStatus(@NonNull String responsibleLogin, @NonNull TaskStatus status) {
        return taskRepositoryJpa.findAllByResponsibleAndStatus(responsibleLogin, status);
    }

}
