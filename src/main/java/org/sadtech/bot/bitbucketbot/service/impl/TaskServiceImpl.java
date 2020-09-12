package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import org.sadtech.basic.core.service.AbstractSimpleManagerService;
import org.sadtech.basic.core.util.Assert;
import org.sadtech.bot.bitbucketbot.domain.change.task.TaskNewChange;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.entity.Task;
import org.sadtech.bot.bitbucketbot.exception.NotFoundException;
import org.sadtech.bot.bitbucketbot.repository.TaskRepository;
import org.sadtech.bot.bitbucketbot.service.ChangeService;
import org.sadtech.bot.bitbucketbot.service.PersonService;
import org.sadtech.bot.bitbucketbot.service.PullRequestsService;
import org.sadtech.bot.bitbucketbot.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class TaskServiceImpl extends AbstractSimpleManagerService<Task, Long> implements TaskService {

    private final TaskRepository taskRepository;

    private final PullRequestsService pullRequestsService;
    private final ChangeService changeService;
    private final PersonService personService;

    public TaskServiceImpl(TaskRepository taskRepository, PullRequestsService pullRequestsService, ChangeService changeService, PersonService personService) {
        super(taskRepository);
        this.taskRepository = taskRepository;
        this.pullRequestsService = pullRequestsService;
        this.changeService = changeService;
        this.personService = personService;
    }

    @Override
    public Task create(@NonNull Task task) {
        Assert.isNotNull(task.getId(), "При создании объекта должен быть установлен идентификатор");
        task.getComments().clear();
        final Task newTask = taskRepository.save(task);

        final PullRequest pullRequest = pullRequestsService.getById(task.getPullRequestId())
                .orElseThrow(() -> new NotFoundException("ПР не найден"));

        changeService.save(
                TaskNewChange.builder()
                        .authorName(task.getAuthor())
                        .messageTask(task.getDescription())
                        .url(task.getUrl())
                        .telegramIds(
                                personService.getAllTelegramIdByLogin(
                                        Collections.singleton(pullRequest.getAuthorLogin())
                                )
                        )
                        .build()
        );

        return newTask;
    }

    @Override
    public Task update(@NonNull Task task) {
        final Task oldTask = taskRepository.findById(task.getId()).orElseThrow(() -> new NotFoundException("Задача не найдена"));
        oldTask.setStatus(task.getStatus());
        return taskRepository.save(oldTask);
    }

    @Override
    public Long getLastTaskId() {
        return taskRepository.findFirstByOrderByIdDesc().map(Task::getId).orElse(0L);
    }

}
