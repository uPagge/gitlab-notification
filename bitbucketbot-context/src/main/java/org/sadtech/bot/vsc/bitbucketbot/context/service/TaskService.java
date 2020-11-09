package org.sadtech.bot.vsc.bitbucketbot.context.service;

import lombok.NonNull;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.TaskStatus;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Comment;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Task;
import org.sadtech.haiti.context.service.SimpleManagerService;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService extends SimpleManagerService<Task, Long> {

    Long getLastTaskId();

    Task convert(@NonNull Comment comment);

    List<Task> getAllBetweenDate(@NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo);

    List<Task> getAllByResponsibleAndStatus(@NonNull String login, @NonNull TaskStatus open);

}
