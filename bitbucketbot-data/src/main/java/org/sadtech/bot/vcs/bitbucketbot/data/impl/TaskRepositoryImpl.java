package org.sadtech.bot.vcs.bitbucketbot.data.impl;

import lombok.NonNull;
import org.sadtech.bot.vcs.bitbucketbot.data.jpa.TaskRepositoryJpa;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.TaskStatus;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Task;
import org.sadtech.bot.vsc.bitbucketbot.context.repository.TaskRepository;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;
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
