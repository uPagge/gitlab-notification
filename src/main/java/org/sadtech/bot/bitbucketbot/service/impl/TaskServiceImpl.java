package org.sadtech.bot.bitbucketbot.service.impl;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.domain.entity.Task;
import org.sadtech.bot.bitbucketbot.exception.CreateException;
import org.sadtech.bot.bitbucketbot.exception.NotFoundException;
import org.sadtech.bot.bitbucketbot.repository.TaskRepository;
import org.sadtech.bot.bitbucketbot.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    @Override
    public Task create(@NonNull Task task) {
        if (task.getId() == null) {
            return taskRepository.save(task);
        }
        throw new CreateException("При создании объекта не должно быть идентификатора");
    }

    @Override
    public void deleteById(@NonNull Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public Task update(@NonNull Task task) {
        final Task oldTask = taskRepository.findById(task.getId()).orElseThrow(() -> new NotFoundException("Задача не найдена"));
        oldTask.setStatus(task.getStatus());
        return taskRepository.save(oldTask);
    }

    @Override
    public List<Task> createAll(@NonNull Collection<Task> tasks) {
        return tasks.stream().map(this::create).collect(Collectors.toList());
    }

    @Override
    public Long getLastTaskId() {
        return taskRepository.findFirstByOrderByIdDesc().map(Task::getId).orElse(0L);
    }

}
