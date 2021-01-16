package org.sadtech.bot.gitlab.context.service;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.TaskStatus;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.sadtech.haiti.context.service.SimpleManagerService;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService extends SimpleManagerService<Task, Long> {

    Long getLastTaskId();

    Task convert(@NonNull Note note);

    List<Task> getAllBetweenDate(@NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo);

    List<Task> getAllByResponsibleAndStatus(@NonNull String login, @NonNull TaskStatus open);

}
