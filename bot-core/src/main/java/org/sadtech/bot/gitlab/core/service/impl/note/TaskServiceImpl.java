package org.sadtech.bot.gitlab.core.service.impl.note;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.PersonInformation;
import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.sadtech.bot.gitlab.context.domain.notify.task.TaskCloseNotify;
import org.sadtech.bot.gitlab.context.domain.notify.task.TaskNewNotify;
import org.sadtech.bot.gitlab.context.repository.TaskRepository;
import org.sadtech.bot.gitlab.context.service.NotifyService;
import org.sadtech.bot.gitlab.context.service.PersonService;
import org.sadtech.bot.gitlab.context.service.TaskService;
import org.sadtech.haiti.context.exception.NotFoundException;
import org.sadtech.haiti.context.page.Pagination;
import org.sadtech.haiti.context.page.Sheet;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl extends AbstractNoteService<Task> implements TaskService {

    private final TaskRepository taskRepository;
    private final NotifyService notifyService;
    private final PersonInformation personInformation;
    private final PersonService personService;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            NotifyService notifyService,
            PersonInformation personInformation,
            PersonService personService
    ) {
        super(taskRepository, notifyService, personInformation);
        this.taskRepository = taskRepository;
        this.notifyService = notifyService;
        this.personInformation = personInformation;
        this.personService = personService;
    }

    @Override
    public Task create(@NonNull Task task) {
        createPerson(task);

        final Task newTask = taskRepository.save(task);
        notifyNewTask(task);
        notificationPersonal(task);
        return newTask;
    }

    private void createPerson(@NonNull Task task) {
        personService.create(task.getAuthor());
        if (task.getResolvedBy() != null) {
            personService.create(task.getResolvedBy());
        }
        personService.create(task.getResponsible());
    }

    @Override
    public Task update(@NonNull Task task) {
        final Task oldTask = taskRepository.findById(task.getId())
                .orElseThrow(() -> new NotFoundException("Задача не найдена"));

        task.setMergeRequest(oldTask.getMergeRequest());
        task.setWebUrl(oldTask.getWebUrl());
        task.setResponsible(oldTask.getResponsible());

        notifyUpdateStatus(oldTask, task);

        return taskRepository.save(task);

    }

    private void notifyUpdateStatus(Task oldTask, Task task) {
        if (
                personInformation.getId().equals(oldTask.getAuthor().getId())
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
                && !personInformation.getId().equals(task.getAuthor().getId())
                && task.getResolved() != null && !task.getResolved()
        ) {
            notifyService.send(
                    TaskNewNotify.builder()
                            .authorName(task.getAuthor().getName())
                            .messageTask(task.getBody())
                            .url(task.getWebUrl())
                            .build()
            );
        }
    }

    @Override
    public Sheet<Task> getAllByResolved(boolean resolved, @NonNull Pagination pagination) {
        return taskRepository.findAllByResolved(resolved, pagination);
    }

    @Override
    public List<Task> getAllPersonTask(@NonNull Long userId, boolean resolved) {
        return taskRepository.findAllByResponsibleIdAndResolved(userId, resolved);
    }

}
