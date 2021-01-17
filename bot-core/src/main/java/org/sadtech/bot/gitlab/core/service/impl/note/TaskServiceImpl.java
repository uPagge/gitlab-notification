package org.sadtech.bot.gitlab.core.service.impl.note;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.PersonInformation;
import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.sadtech.bot.gitlab.context.domain.notify.task.TaskCloseNotify;
import org.sadtech.bot.gitlab.context.domain.notify.task.TaskNewNotify;
import org.sadtech.bot.gitlab.context.repository.TaskRepository;
import org.sadtech.bot.gitlab.context.service.NotifyService;
import org.sadtech.bot.gitlab.context.service.TaskService;
import org.sadtech.haiti.context.exception.NotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends AbstractNoteService<Task> implements TaskService {

    private final TaskRepository taskRepository;
    private final NotifyService notifyService;
    private final PersonInformation personInformation;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            NotifyService notifyService,
            PersonInformation personInformation) {
        super(taskRepository, notifyService, personInformation);
        this.taskRepository = taskRepository;
        this.notifyService = notifyService;
        this.personInformation = personInformation;
    }

    @Override
    public Task create(@NonNull Task task) {
        final Task newTask = taskRepository.save(task);
        notifyNewTask(task);
        notificationPersonal(task);
        return newTask;
    }

    @Override
    public Task update(@NonNull Task task) {
        final Task oldTask = taskRepository.findById(task.getId())
                .orElseThrow(() -> new NotFoundException("Задача не найдена"));

        if (oldTask.getUpdated().equals(task.getUpdated())) {

            task.setWebUrl(oldTask.getWebUrl());
            task.setResponsible(oldTask.getResponsible());

            notifyUpdateStatus(oldTask, task);

            return taskRepository.save(oldTask);
        }
        return oldTask;
    }

    private void notifyUpdateStatus(Task oldTask, Task task) {
        if (
                personInformation.getId().equals(oldTask.getAuthor().getId())
                        && !personInformation.getId().equals(oldTask.getResolvedBy().getId())

        ) {
            final boolean oldStatus = oldTask.getResolved();
            final boolean newStatus = task.getResolved();
            if (!oldStatus && newStatus) {
                notifyService.send(
                        TaskCloseNotify.builder()
                                .authorName(task.getAuthor().getName())
                                .messageTask(task.getBody())
                                .url(task.getWebUrl())
                                .build()
                );
            }
        }
    }

    private void notifyNewTask(Task task) {
        if (personInformation.getId().equals(task.getResponsible().getId())
                && !personInformation.getId().equals(task.getAuthor().getId())) {
            notifyService.send(
                    TaskNewNotify.builder()
                            .authorName(task.getAuthor().getName())
                            .messageTask(task.getBody())
                            .url(task.getWebUrl())
                            .build()
            );
        }
    }

}
