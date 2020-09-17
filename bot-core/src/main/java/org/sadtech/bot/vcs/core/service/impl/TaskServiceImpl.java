package org.sadtech.bot.vcs.core.service.impl;

import lombok.NonNull;
import org.sadtech.basic.core.service.AbstractSimpleManagerService;
import org.sadtech.basic.core.util.Assert;
import org.sadtech.bot.vcs.core.domain.Answer;
import org.sadtech.bot.vcs.core.domain.TaskStatus;
import org.sadtech.bot.vcs.core.domain.change.comment.AnswerCommentChange;
import org.sadtech.bot.vcs.core.domain.change.comment.CommentChange;
import org.sadtech.bot.vcs.core.domain.change.task.TaskCloseChange;
import org.sadtech.bot.vcs.core.domain.change.task.TaskNewChange;
import org.sadtech.bot.vcs.core.domain.entity.Comment;
import org.sadtech.bot.vcs.core.domain.entity.PullRequest;
import org.sadtech.bot.vcs.core.domain.entity.Task;
import org.sadtech.bot.vcs.core.exception.NotFoundException;
import org.sadtech.bot.vcs.core.repository.TaskRepository;
import org.sadtech.bot.vcs.core.service.ChangeService;
import org.sadtech.bot.vcs.core.service.CommentService;
import org.sadtech.bot.vcs.core.service.PersonService;
import org.sadtech.bot.vcs.core.service.PullRequestsService;
import org.sadtech.bot.vcs.core.service.TaskService;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl extends AbstractSimpleManagerService<Task, Long> implements TaskService {

    private static final Pattern PATTERN = Pattern.compile("@[\\w]+");

    private final TaskRepository taskRepository;

    private final PullRequestsService pullRequestsService;
    private final ChangeService changeService;
    private final PersonService personService;
    private final CommentService commentService;

    private final ConversionService conversionService;

    public TaskServiceImpl(
            TaskRepository taskRepository,
            PullRequestsService pullRequestsService,
            ChangeService changeService,
            PersonService personService,
            CommentService commentService,
            ConversionService conversionService
    ) {
        super(taskRepository);
        this.taskRepository = taskRepository;
        this.pullRequestsService = pullRequestsService;
        this.changeService = changeService;
        this.personService = personService;
        this.commentService = commentService;
        this.conversionService = conversionService;
    }

    @Override
    public Task create(@NonNull Task task) {
        Assert.isNotNull(task.getId(), "При создании объекта должен быть установлен идентификатор");
        task.getAnswers().clear();
        final Task newTask = taskRepository.save(task);
        notifyNewTask(task);
        notificationPersonal(task);
        return newTask;
    }

    @Override
    public Task update(@NonNull Task task) {
        final Task oldTask = taskRepository.findById(task.getId())
                .orElseThrow(() -> new NotFoundException("Задача не найдена"));

        if (!task.getBitbucketVersion().equals(oldTask.getBitbucketVersion())) {
            oldTask.setDescription(task.getDescription());
            oldTask.setBitbucketVersion(task.getBitbucketVersion());
        }
        updateAnswer(oldTask, task);
        updateStatus(oldTask, task);
        oldTask.setStatus(task.getStatus());
        return taskRepository.save(oldTask);
    }

    private void updateStatus(Task oldTask, Task task) {
        final TaskStatus oldStatus = oldTask.getStatus();
        final TaskStatus newStatus = task.getStatus();
        if (!oldStatus.equals(newStatus)) {
            switch (newStatus) {
                case OPEN:
                    changeService.save(
                            TaskNewChange.builder()
                                    .messageTask(task.getDescription())
                                    .authorName(oldTask.getAuthor())
                                    .url(oldTask.getUrl())
                                    .telegramIds(
                                            personService.getAllTelegramIdByLogin(Collections.singleton(oldTask.getResponsible()))
                                    )
                                    .build()
                    );
                    break;
                case RESOLVED:
                    changeService.save(
                            TaskCloseChange.builder()
                                    .messageTask(oldTask.getDescription())
                                    .authorName(oldTask.getAuthor())
                                    .url(oldTask.getUrl())
                                    .telegramIds(
                                            personService.getAllTelegramIdByLogin(Collections.singleton(oldTask.getAuthor()))
                                    )
                                    .build()
                    );
                    break;
                default:
                    throw new NotFoundException("Обработчика типа не существует");
            }
            oldTask.setStatus(newStatus);
        }
    }

    private void updateAnswer(Task oldTask, Task task) {
        final Set<Long> oldAnswerIds = oldTask.getAnswers();
        final Set<Long> newAnswerIds = task.getAnswers();
        if (!oldAnswerIds.equals(newAnswerIds)) {
            final Set<Long> existsNewAnswersIds = commentService.existsById(newAnswerIds);
            final List<Comment> newAnswers = commentService.getAllById(existsNewAnswersIds).stream()
                    .filter(comment -> !oldAnswerIds.contains(comment.getId()))
                    .collect(Collectors.toList());
            oldTask.getAnswers().clear();
            oldTask.setAnswers(existsNewAnswersIds);
            changeService.save(
                    AnswerCommentChange.builder()
                            .telegramIds(
                                    personService.getAllTelegramIdByLogin(Collections.singleton(oldTask.getAuthor()))
                            )
                            .url(task.getUrl())
                            .youMessage(oldTask.getDescription())
                            .answers(
                                    newAnswers.stream()
                                            .map(answerComment -> Answer.of(answerComment.getAuthor(), answerComment.getMessage()))
                                            .collect(Collectors.toList())
                            )
                            .build()
            );
        }
    }

    @Override
    public Long getLastTaskId() {
        return taskRepository.findFirstByOrderByIdDesc().map(Task::getId).orElse(0L);
    }

    @Override
    public Task convert(@NonNull Comment comment) {
        commentService.deleteById(comment.getId());
        final Task task = conversionService.convert(comment, Task.class);
        final Task newTask = taskRepository.save(task);
        notifyNewTask(newTask);
        return newTask;
    }

    @Override
    public List<Task> getAllBetweenDate(@NonNull LocalDateTime dateFrom, @NonNull LocalDateTime dateTo) {
        return taskRepository.findByCreateDateBetween(dateFrom, dateTo);
    }

    private void notifyNewTask(Task task) {
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
    }

    private void notificationPersonal(@NonNull Task task) {
        Matcher matcher = PATTERN.matcher(task.getDescription());
        Set<String> recipientsLogins = new HashSet<>();
        while (matcher.find()) {
            final String login = matcher.group(0).replace("@", "");
            recipientsLogins.add(login);
        }
        final Set<Long> recipientsIds = personService.getAllTelegramIdByLogin(recipientsLogins);
        changeService.save(
                CommentChange.builder()
                        .authorName(task.getAuthor())
                        .url(task.getUrl())
                        .telegramIds(recipientsIds)
                        .message(task.getDescription())
                        .build()
        );
    }

}
