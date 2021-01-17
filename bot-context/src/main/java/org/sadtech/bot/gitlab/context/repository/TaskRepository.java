package org.sadtech.bot.gitlab.context.repository;

import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;

public interface TaskRepository extends SimpleManagerRepository<Task, Long> {

}
