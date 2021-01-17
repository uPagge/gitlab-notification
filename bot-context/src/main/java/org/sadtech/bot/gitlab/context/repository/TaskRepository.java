package org.sadtech.bot.gitlab.context.repository;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;

public interface TaskRepository extends SimpleManagerRepository<Task, Long> {

    Sheet<Task> findAllByResolved(boolean resolved, @NonNull Pagination pagination);

}
