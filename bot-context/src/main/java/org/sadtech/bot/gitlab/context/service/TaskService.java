package org.sadtech.bot.gitlab.context.service;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.context.service.SimpleManagerService;

public interface TaskService extends SimpleManagerService<Task, Long> {

    Sheet<Task> getAllByResolved(boolean resolved, @NonNull Pagination pagination);

}
