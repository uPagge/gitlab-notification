package org.sadtech.bot.bitbucketbot.service;

import lombok.NonNull;
import org.sadtech.basic.context.service.SimpleManagerService;
import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.sadtech.bot.bitbucketbot.domain.entity.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService extends SimpleManagerService<Task, Long> {

    Long getLastTaskId();

    Task convert(@NonNull Comment comment);

    List<Task> getAllBetweenDate(@NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo);

}
