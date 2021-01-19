package org.sadtech.bot.gitlab.data.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.sadtech.bot.gitlab.context.repository.TaskRepository;
import org.sadtech.bot.gitlab.data.jpa.TaskRepositoryJpa;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.sadtech.haiti.database.util.Converter;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TaskRepositoryImpl extends AbstractSimpleManagerRepository<Task, Long> implements TaskRepository {

    private final TaskRepositoryJpa taskRepositoryJpa;

    public TaskRepositoryImpl(TaskRepositoryJpa taskRepositoryJpa) {
        super(taskRepositoryJpa);
        this.taskRepositoryJpa = taskRepositoryJpa;
    }

    @Override
    public Sheet<Task> findAllByResolved(boolean resolved, @NonNull Pagination pagination) {
        return Converter.page(
                taskRepositoryJpa.findAllByResolved(resolved, Converter.pagination(pagination))
        );
    }

    @Override
    public List<Task> findAllByResponsibleIdAndResolved(@NonNull Long userId, boolean resolved) {
        return taskRepositoryJpa.findAllByResponsibleIdAndResolved(userId, resolved);
    }
}
