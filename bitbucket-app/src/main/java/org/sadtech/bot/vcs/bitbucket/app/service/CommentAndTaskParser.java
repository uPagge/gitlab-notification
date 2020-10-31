package org.sadtech.bot.vcs.bitbucket.app.service;

import lombok.NonNull;
import org.sadtech.basic.context.exception.NotFoundException;
import org.sadtech.basic.context.page.Sheet;
import org.sadtech.basic.core.page.PaginationImpl;
import org.sadtech.bot.vcs.bitbucket.app.config.property.CommentSchedulerProperty;
import org.sadtech.bot.vcs.bitbucket.app.service.executor.DataScan;
import org.sadtech.bot.vcs.bitbucket.sdk.domain.CommentJson;
import org.sadtech.bot.vcs.bitbucket.sdk.domain.Severity;
import org.sadtech.bot.vcs.core.config.properties.BitbucketProperty;
import org.sadtech.bot.vcs.core.config.properties.InitProperty;
import org.sadtech.bot.vcs.core.utils.Utils;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Comment;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.PullRequest;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.PullRequestMini;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.Task;
import org.sadtech.bot.vsc.bitbucketbot.context.service.CommentService;
import org.sadtech.bot.vsc.bitbucketbot.context.service.PullRequestsService;
import org.sadtech.bot.vsc.bitbucketbot.context.service.TaskService;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>Поиск новых комментариев и задач.</p>
 * <p>К несчастью, у битбакета не очень удобный API, и у них таска это то же самое что и комментарий, только с флагом</p>
 */
@Component
public class CommentAndTaskParser {

    private final CommentService commentService;
    private final PullRequestsService pullRequestsService;
    private final ExecutorScanner executorScanner;
    private final TaskService taskService;
    private final ConversionService conversionService;

    private final BitbucketProperty bitbucketProperty;
    private final CommentSchedulerProperty commentSchedulerProperty;
    private final InitProperty initProperty;

    private boolean initStart = false;

    public CommentAndTaskParser(
            CommentService commentService,
            PullRequestsService pullRequestsService,
            ExecutorScanner executorScanner,
            TaskService taskService,
            ConversionService conversionService,
            BitbucketProperty bitbucketProperty,
            CommentSchedulerProperty commentSchedulerProperty,
            InitProperty initProperty
    ) {
        this.commentService = commentService;
        this.pullRequestsService = pullRequestsService;
        this.executorScanner = executorScanner;
        this.taskService = taskService;
        this.conversionService = conversionService;
        this.bitbucketProperty = bitbucketProperty;
        this.commentSchedulerProperty = commentSchedulerProperty;
        this.initProperty = initProperty;
    }

    public void scanNewCommentAndTask() {
        long commentId = getLastIdCommentOrTask() + 1;
        int count = 0;
        do {
            final List<DataScan> dataScans = generatingLinksToPossibleComments(commentId);
            executorScanner.registration(dataScans);
            final List<CommentJson> resultScans = executorScanner.getResult();
            if (!resultScans.isEmpty()) {
                final long commentMax = commentService.createAll(getCommentsByResultScan(resultScans)).stream()
                        .mapToLong(Comment::getId)
                        .max().orElse(0L);
                final long taskMax = taskService.createAll(getTaskByResultScan(resultScans)).stream()
                        .mapToLong(Task::getId)
                        .max().orElse(0L);
                commentId = Long.max(commentMax, taskMax) + 1;
                count = 0;
            }
        } while (count++ < commentSchedulerProperty.getNoCommentCount());
    }

    private long getLastIdCommentOrTask() {
        Long commentStartId = Long.max(commentService.getLastCommentId(), taskService.getLastTaskId());
        if (initProperty != null && !initStart && (commentStartId == 0L || initProperty.isUse())) {
            commentStartId = initProperty.getStartCommentId();
            initStart = true;
        }
        return commentStartId;
    }

    private List<DataScan> generatingLinksToPossibleComments(@NonNull Long commentId) {
        List<DataScan> commentUrls = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int page = 0;
            Sheet<PullRequest> pullRequestPage = pullRequestsService.getAll(
                    PaginationImpl.of(page, commentSchedulerProperty.getCommentCount())
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
                        PaginationImpl.of(++page, commentSchedulerProperty.getCommentCount())
                );
            }
            commentId++;
        }
        return commentUrls;
    }

    private List<Comment> getCommentsByResultScan(List<CommentJson> commentJsons) {
        return commentJsons.stream()
                .filter(json -> Severity.NORMAL.equals(json.getSeverity()))
                .map(resultScan -> conversionService.convert(resultScan, Comment.class))
                .peek(
                        comment -> {
                            final PullRequestMini pullRequestMini = pullRequestsService.getMiniInfo(comment.getPullRequestId())
                                    .orElseThrow(() -> new NotFoundException("Автор ПР не найден"));
                            comment.setUrl(generateUrl(comment.getId(), pullRequestMini.getUrl()));
                            comment.setResponsible(pullRequestMini.getAuthorLogin());
                        }
                )
                .collect(Collectors.toList());
    }

    private List<Task> getTaskByResultScan(List<CommentJson> commentJsons) {
        return commentJsons.stream()
                .filter(json -> Severity.BLOCKER.equals(json.getSeverity()))
                .map(resultScan -> conversionService.convert(resultScan, Task.class))
                .peek(
                        task -> {
                            final PullRequestMini pullRequestMini = pullRequestsService.getMiniInfo(task.getPullRequestId())
                                    .orElseThrow(() -> new NotFoundException("Автор ПР не найден"));
                            task.setResponsible(pullRequestMini.getAuthorLogin());
                            task.setUrl(generateUrl(task.getId(), pullRequestMini.getUrl()));
                        }
                )
                .collect(Collectors.toList());
    }

    private String generateUrl(@NonNull Long id, @NonNull String pullRequestUrl) {
        return MessageFormat.format("{0}/overview?commentId={1}", pullRequestUrl, Long.toString(id));
    }

    private String getCommentUrl(long commentId, PullRequest pullRequest) {
        return bitbucketProperty.getUrlPullRequestComment()
                .replace("{projectKey}", pullRequest.getProjectKey())
                .replace("{repositorySlug}", pullRequest.getRepositorySlug())
                .replace("{pullRequestId}", pullRequest.getBitbucketId().toString())
                .replace("{commentId}", String.valueOf(commentId));
    }

    public void scanOldComment() {
        final List<Comment> comments = commentService.getAllBetweenDate(
                LocalDateTime.now().minusDays(20), LocalDateTime.now()
        );
        for (Comment oldComment : comments) {
            final Optional<CommentJson> optCommentJson = Utils.urlToJson(
                    oldComment.getUrlApi(),
                    bitbucketProperty.getToken(),
                    CommentJson.class
            );
            if (optCommentJson.isPresent()) {
                final CommentJson json = optCommentJson.get();
                if (Severity.BLOCKER.equals(json.getSeverity())) {
                    taskService.convert(oldComment);
                } else {
                    final Comment newComment = conversionService.convert(json, Comment.class);
                    commentService.update(newComment);
                }
            } else {
                commentService.deleteById(oldComment.getId());
            }
        }
    }

    public void scanOldTask() {
        final List<Task> tasks = taskService.getAllBetweenDate(
                LocalDateTime.now().minusDays(20), LocalDateTime.now()
        );
        for (Task oldTask : tasks) {
            final Optional<CommentJson> optCommentJson = Utils.urlToJson(
                    oldTask.getUrlApi(),
                    bitbucketProperty.getToken(),
                    CommentJson.class
            );
            if (optCommentJson.isPresent()) {
                final CommentJson json = optCommentJson.get();
                if (Severity.NORMAL.equals(json.getSeverity())) {
                    commentService.convert(oldTask);
                } else {
                    final Task newTask = conversionService.convert(json, Task.class);
                    taskService.update(newTask);
                }
            } else {
                taskService.deleteById(oldTask.getId());
            }
        }
    }

}
