package org.sadtech.bot.bitbucketbot.service.parser;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.bot.bitbucketbot.config.properties.BitbucketProperty;
import org.sadtech.bot.bitbucketbot.config.properties.CommentSchedulerProperty;
import org.sadtech.bot.bitbucketbot.domain.Answer;
import org.sadtech.bot.bitbucketbot.domain.Pagination;
import org.sadtech.bot.bitbucketbot.domain.change.ChangeType;
import org.sadtech.bot.bitbucketbot.domain.change.comment.AnswerCommentChange;
import org.sadtech.bot.bitbucketbot.domain.change.comment.CommentChange;
import org.sadtech.bot.bitbucketbot.domain.change.task.TaskChange;
import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.entity.Task;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.CommentJson;
import org.sadtech.bot.bitbucketbot.dto.bitbucket.Severity;
import org.sadtech.bot.bitbucketbot.service.ChangeService;
import org.sadtech.bot.bitbucketbot.service.CommentService;
import org.sadtech.bot.bitbucketbot.service.PersonService;
import org.sadtech.bot.bitbucketbot.service.PullRequestsService;
import org.sadtech.bot.bitbucketbot.service.TaskService;
import org.sadtech.bot.bitbucketbot.service.Utils;
import org.sadtech.bot.bitbucketbot.service.executor.DataScan;
import org.sadtech.bot.bitbucketbot.service.executor.ResultScan;
import org.sadtech.bot.bitbucketbot.service.impl.ExecutorScanner;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

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
 * <p>Поиск новых комментариев и задач.</p>
 * <p>К несчастью, у битбакета не очень удобный API, и у них таска это то же самое что и комментарий, только с флагом</p>
 */
@Component
@RequiredArgsConstructor
public class CommentAndTaskParser {

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

    public void scanNewCommentAndTask() {
        long commentId = getLastIdCommentOrTask() + 1;
        int count = 0;
        do {
            final List<DataScan> dataScans = generatingLinksToPossibleComments(commentId);
            executorScanner.registration(dataScans);
            final List<ResultScan> resultScans = executorScanner.getResult();
            if (!resultScans.isEmpty()) {
                processingComments(resultScans);
                processingTasks(resultScans);
                count = 0;
            }
        } while (count++ < commentSchedulerProperty.getNoCommentCount());
    }

    private long getLastIdCommentOrTask() {
        return Long.max(commentService.getLastCommentId(), taskService.getLastTaskId());
    }

    private void processingComments(List<ResultScan> resultScans) {
        final List<Comment> newComments = commentService.createAll(getCommentsByResultScan(resultScans));
        newComments.forEach(this::notificationPersonal);
    }

    private void processingTasks(List<ResultScan> resultScans) {
        final List<Task> newTasks = taskService.createAll(getTaskByResultScan(resultScans));
        newTasks.forEach(this::notificationNewTask);
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

    private List<Comment> getCommentsByResultScan(List<ResultScan> resultScans) {
        return resultScans.stream()
                .filter(resultScan -> Severity.NORMAL.equals(resultScan.getCommentJson().getSeverity()))
                .map(resultScan -> conversionService.convert(resultScan, Comment.class))
                .collect(Collectors.toList());
    }

    private List<Task> getTaskByResultScan(List<ResultScan> resultScans) {
        return resultScans.stream()
                .filter(commentJson -> Severity.BLOCKER.equals(commentJson.getCommentJson().getSeverity()))
                .map(resultScan -> conversionService.convert(resultScan, Task.class))
                .collect(Collectors.toList());
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
                        .url(comment.getUrl())
                        .telegramIds(recipientsIds)
                        .message(comment.getMessage())
                        .build()
        );
    }

    private void notificationNewTask(@NonNull Task task) {
        changeService.add(
                TaskChange.builder()
                        .authorName(task.getAuthor().getFullName())
                        .messageTask(task.getDescription())
                        .type(ChangeType.NEW_TASK)
                        .url(task.getUrl())
                        .telegramIds(Collections.singleton(task.getPullRequest().getAuthor().getTelegramId()))
                        .build()
        );
    }

    public void scanOldComment() {
        @NonNull final List<Comment> comments = commentService.getAllBetweenDate(
                LocalDateTime.now().minusDays(10), LocalDateTime.now()
        );
        for (Comment oldComment : comments) {
            final Optional<CommentJson> optCommentJson = Utils.urlToJson(
                    oldComment.getUrl(),
                    bitbucketProperty.getToken(),
                    CommentJson.class
            );
            final Comment newComment = commentService.update(conversionService.convert(oldComment, Comment.class));

            if (optCommentJson.isPresent()) {
                final CommentJson commentJson = optCommentJson.get();
                notifyNewCommentAnswers(oldComment, newComment);
            }
        }
    }

    private void notifyNewCommentAnswers(Comment oldComment, Comment newComment) {
        final Set<Long> oldAnswerIds = oldComment.getAnswers();
        final Set<Long> newAnswerIds = newComment.getAnswers();
        if (!newAnswerIds.isEmpty()) {
            final List<Comment> newAnswers = commentService.getAllById(newAnswerIds).stream()
                    .filter(comment -> !oldAnswerIds.contains(comment.getId()))
                    .collect(Collectors.toList());
            changeService.add(
                    AnswerCommentChange.builder()
                            .telegramIds(
                                    Collections.singleton(newComment.getAuthor().getTelegramId())
                            )
                            .url(newComment.getPullRequest().getUrl())
                            .youMessage(newComment.getMessage())
                            .answers(
                                    newAnswers.stream()
                                            .map(comment -> Answer.of(comment.getAuthor().getFullName(), comment.getMessage()))
                                            .collect(Collectors.toList())
                            )
                            .build()
            );
        }
    }

}
