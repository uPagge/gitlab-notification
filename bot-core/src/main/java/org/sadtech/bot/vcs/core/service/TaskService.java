package org.sadtech.bot.vcs.core.service;

import lombok.NonNull;
import org.sadtech.basic.context.service.SimpleManagerService;
import org.sadtech.bot.vcs.core.domain.TaskStatus;
import org.sadtech.bot.vcs.core.domain.entity.Comment;
import org.sadtech.bot.vcs.core.domain.entity.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService extends SimpleManagerService<Task, Long> {

    Long getLastTaskId();

    Task convert(@NonNull Comment comment);

    List<Task> getAllBetweenDate(@NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo);

    List<Task> getAllByResponsibleAndStatus(@NonNull String login, @NonNull TaskStatus open);

}
