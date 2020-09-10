package org.sadtech.bot.bitbucketbot.service;

import org.sadtech.basic.context.service.SimpleManagerService;
import org.sadtech.bot.bitbucketbot.domain.entity.Task;

public interface TaskService extends SimpleManagerService<Task, Long> {

    Long getLastTaskId();

}
