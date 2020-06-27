package org.sadtech.bot.bitbucketbot.scheduler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.bitbucketbot.config.properties.BitbucketProperty;
import org.sadtech.bot.bitbucketbot.config.properties.CommentSchedulerProperty;
import org.sadtech.bot.bitbucketbot.domain.Answer;
import org.sadtech.bot.bitbucketbot.domain.Pagination;
import org.sadtech.bot.bitbucketbot.domain.TaskStatus;
import org.sadtech.bot.bitbucketbot.domain.change.ChangeType;
import org.sadtech.bot.bitbucketbot.domain.change.comment.AnswerCommentChange;
import org.sadtech.bot.bitbucketbot.domain.change.comment.CommentChange;
import org.sadtech.bot.bitbucketbot.domain.change.task.TaskChange;
import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.entity.Task;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.CommentJson;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.Severity;
import org.sadtech.bot.bitbucketbot.exception.NotFoundException;
import org.sadtech.bot.bitbucketbot.service.ChangeService;
import org.sadtech.bot.bitbucketbot.service.CommentService;
import org.sadtech.bot.bitbucketbot.service.PersonService;
import org.sadtech.bot.bitbucketbot.service.PullRequestsService;
import org.sadtech.bot.bitbucketbot.service.TaskService;
import org.sadtech.bot.bitbucketbot.service.Utils;
import org.sadtech.bot.bitbucketbot.service.executor.DataScan;
import org.sadtech.bot.bitbucketbot.service.executor.ResultScan;
import org.sadtech.bot.bitbucketbot.service.impl.ExecutorScanner;
import org.sadtech.bot.bitbucketbot.utils.Converter;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Шедулер отвечает за работу с комментариями. Поиск новых комментариев, проверка старых. Так как таски в
 * битбакете реализуются через комментарии, то <b>этот шедулер так же работает с тасками</b>.
 *
 * @author upagge
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerComments {

    private static final Pattern PATTERN = Pattern.compile("@[\\w]+");

    private final CommentService commentService;
    private final PullRequestsService pullRequestsService;
    private final PersonService personService;
    private final ChangeService changeService;
    private final ExecutorScanner executorScanner;
    private final TaskService taskService;
    private final ConversionService conversionService;

    private final BitbucketProperty bitbucketProperty;
    private final CommentSchedulerProperty commentSchedulerProperty;

    /**
     * Сканирует появление новых комментариев
     */
    @Scheduled(cron = "0 */3 * * * *")
    public void newComments() {
        long commentId = commentService.getLastCommentId() + 1;
        int count = 0;
        do {
            final List<DataScan> dataScans = generatingLinksToPossibleComments(commentId);
            executorScanner.registration(dataScans);
            final List<ResultScan> resultScans = executorScanner.getResult();
            if (!resultScans.isEmpty()) {
                final List<Comment> comments = getCommentsByResultScan(resultScans);
                final List<Comment> newComments = commentService.createAll(comments);
                checkNewTask(newComments);
                notificationPersonal(newComments);
                count = 0;
            }
        } while (count++ < commentSchedulerProperty.getNoCommentCount());
    }

    private List<Comment> getCommentsByResultScan(List<ResultScan> resultScans) {
        return resultScans.stream()
                .map(result -> conversionService.convert(result, Comment.class))
                .collect(Collectors.toList());
    }

    private List<DataScan> generatingLinksToPossibleComments(@NonNull Long commentId) {
        List<DataScan> commentUrls = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int page = 0;
            Page<PullRequest> pullRequestPage = pullRequestsService.getAll(
                    Pagination.of(page, commentSchedulerProperty.getCommentCount())
            );
            while (pullRequestPage.hasContent()) {
                long finalCommentId = commentId;
                commentUrls.addAll(pullRequestPage.getContent().stream()
                        .map(
                                pullRequest -> new DataScan(
                                        getCommentUrl(finalCommentId, pullRequest),
                                        pullRequest.getId()
                                )
                        )
                        .collect(Collectors.toList()));
                pullRequestPage = pullRequestsService.getAll(
                        Pagination.of(++page, commentSchedulerProperty.getCommentCount())
                );
            }
            commentId++;
        }
        return commentUrls;
    }


    private void checkNewTask(CommentJson commentJson, String urlPr, String authorLoginPr) {
        if (Severity.BLOCKER.equals(commentJson.getSeverity())) {
            final Task task = new Task();
            task.setStatus(Converter.taskStatus(commentJson.getState()));
            task.setComment(commentService.getProxyById(commentJson.getId()).orElseThrow(() -> new NotFoundException("Неожиданная ошибка")));

            taskService.create(task);

            if (TaskStatus.OPEN.equals(task.getStatus())) {
                changeService.add(
                        TaskChange.builder()
                                .type(ChangeType.NEW_TASK)
                                .authorName(commentJson.getAuthor().getDisplayName())
                                .messageTask(commentJson.getText())
                                .telegramIds(personService.getAllTelegramIdByLogin(Collections.singleton(authorLoginPr)))
                                .url(urlPr)
                                .build()
                );
            }
        }
    }


    /**
     * Проверяет состояние старых комментариев
     */
    @Scheduled(cron = "0 */1 * * * *")
    public void oldComments() {
        @NonNull final List<Comment> comments = commentService.getAllBetweenDate(
                LocalDateTime.now().minusDays(10), LocalDateTime.now()
        );
        for (Comment comment : comments) {
            final Optional<CommentJson> optCommentJson = Utils.urlToJson(
                    comment.getUrl(),
                    bitbucketProperty.getToken(),
                    CommentJson.class
            );
            if (optCommentJson.isPresent()) {
                final CommentJson commentJson = optCommentJson.get();
                checkNewAnswers(comment, commentJson);
                checkOldTask(comment, commentJson);
            }
        }
    }

    private void checkOldTask(Comment comment, CommentJson commentJson) {
        final Task task = comment.getTask();
        if (task == null) {
            checkNewTask(commentJson, comment.getPrUrl(), commentJson.getAuthor().getName());
        } else {
            if (Severity.NORMAL.equals(commentJson.getSeverity())) {
                taskService.deleteById(comment.getId());

                changeService.add(
                        TaskChange.builder()
                                .type(ChangeType.DELETED_TASK)
                                .telegramIds(personService.getAllTelegramIdByLogin(Collections.singleton(commentJson.getAuthor().getName())))
                                .authorName(commentJson.getAuthor().getDisplayName())
                                .url(comment.getPrUrl())
                                .messageTask(commentJson.getText())
                                .build()
                );
            } else {
                final TaskStatus taskStatus = task.getStatus();
                final TaskStatus newTaskStatus = Converter.taskStatus(commentJson.getState());
                task.setStatus(newTaskStatus);
                taskService.update(task);
                if (!taskStatus.equals(newTaskStatus)) {
                    changeService.add(
                            TaskChange.builder()
                                    .type(TaskStatus.RESOLVED.equals(newTaskStatus) ? ChangeType.RESOLVED_TASK : ChangeType.OPEN_TASK)
                                    .authorName(commentJson.getAuthor().getDisplayName())
                                    .url(comment.getPrUrl())
                                    .messageTask(commentJson.getText())
                                    .telegramIds(
                                            TaskStatus.RESOLVED.equals(newTaskStatus)
                                                    ? personService.getAllTelegramIdByLogin(Collections.singleton(commentJson.getAuthor().getName()))
                                                    : personService.getAllTelegramIdByLogin(Collections.singleton(commentJson.getAuthor().getName()))
                                    )
                                    .build()
                    );
                }
            }
        }
    }

    private void checkNewAnswers(Comment comment, CommentJson commentJson) {
        final Set<Long> oldAnswerIds = comment.getAnswers();
        final List<CommentJson> newAnswers = commentJson.getComments().stream()
                .filter(answerJson -> !oldAnswerIds.contains(answerJson.getId()))
                .collect(Collectors.toList());
        if (!newAnswers.isEmpty()) {
            changeService.add(
                    AnswerCommentChange.builder()
                            .telegramIds(
                                    personService.getTelegramIdByLogin(commentJson.getAuthor().getName())
                                            .map(Collections::singleton)
                                            .orElse(Collections.emptySet())
                            )
                            .url(comment.getPrUrl())
                            .youMessage(commentJson.getText())
                            .answers(
                                    newAnswers.stream()
                                            .map(json -> Answer.of(json.getAuthor().getName(), json.getText()))
                                            .collect(Collectors.toList())
                            )
                            .build()
            );
            comment.getAnswers().addAll(newAnswers.stream().map(CommentJson::getId).collect(Collectors.toList()));
            commentService.save(comment);
        }
    }

    private String getCommentUrl(long commentId, PullRequest pullRequest) {
        return bitbucketProperty.getUrlPullRequestComment()
                .replace("{projectKey}", pullRequest.getProjectKey())
                .replace("{repositorySlug}", pullRequest.getRepositorySlug())
                .replace("{pullRequestId}", pullRequest.getBitbucketId().toString())
                .replace("{commentId}", String.valueOf(commentId));
    }

    private void notificationPersonal(@NonNull Comment comment) {
        Matcher matcher = PATTERN.matcher(comment.getMessage());
        Set<String> recipientsLogins = new HashSet<>();
        while (matcher.find()) {
            final String login = matcher.group(0).replace("@", "");
            recipientsLogins.add(login);
        }
        final Set<Long> recipientsIds = personService.getAllTelegramIdByLogin(recipientsLogins);
        changeService.add(
                CommentChange.builder()
                        .authorName(comment.getAuthor().getLogin())
                        .url(comment.getPullRequest())
                        .telegramIds(recipientsIds)
                        .message(comment.getMessage())
                        .build()
        );
    }

}
