package org.sadtech.bot.bitbucketbot.service.parser;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.sadtech.basic.context.page.Sheet;
import org.sadtech.basic.core.page.PaginationImpl;
import org.sadtech.bot.bitbucketbot.config.InitProperty;
import org.sadtech.bot.bitbucketbot.config.properties.BitbucketProperty;
import org.sadtech.bot.bitbucketbot.config.properties.CommentSchedulerProperty;
import org.sadtech.bot.bitbucketbot.domain.Answer;
import org.sadtech.bot.bitbucketbot.domain.change.comment.AnswerCommentChange;
import org.sadtech.bot.bitbucketbot.domain.entity.Comment;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequest;
import org.sadtech.bot.bitbucketbot.domain.entity.PullRequestMini;
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
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>Поиск новых комментариев и задач.</p>
 * <p>К несчастью, у битбакета не очень удобный API, и у них таска это то же самое что и комментарий, только с флагом</p>
 */
@Component
@RequiredArgsConstructor
public class CommentAndTaskParser {

    private final CommentService commentService;
    private final PullRequestsService pullRequestsService;
    private final PersonService personService;
    private final ChangeService changeService;
    private final ExecutorScanner executorScanner;
    private final TaskService taskService;
    private final ConversionService conversionService;

    private final BitbucketProperty bitbucketProperty;
    private final CommentSchedulerProperty commentSchedulerProperty;
    private final InitProperty initProperty;

    private boolean initStart = false;

    public void scanNewCommentAndTask() {
        long commentId = getLastIdCommentOrTask() + 1;
        int count = 0;
        do {
            final List<DataScan> dataScans = generatingLinksToPossibleComments(commentId);
            executorScanner.registration(dataScans);
            final List<ResultScan> resultScans = executorScanner.getResult();
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

    private List<Comment> getCommentsByResultScan(List<ResultScan> resultScans) {
        return resultScans.stream()
                .filter(resultScan -> Severity.NORMAL.equals(resultScan.getCommentJson().getSeverity()))
                .map(resultScan -> conversionService.convert(resultScan, Comment.class))
                .peek(
                        comment -> {
                            final PullRequestMini pullRequestMini = pullRequestsService.getMiniInfo(comment.getPullRequestId())
                                    .orElseThrow(() -> new NotFoundException("Автор ПР не найден"));
                            comment.setUrl(generateUrl(comment.getId(), pullRequestMini.getUrl()));
                        }
                )
                .collect(Collectors.toList());
    }

    private List<Task> getTaskByResultScan(List<ResultScan> resultScans) {
        return resultScans.stream()
                .filter(commentJson -> Severity.BLOCKER.equals(commentJson.getCommentJson().getSeverity()))
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
        return MessageFormat.format("{0}/overview?commentId={1}", pullRequestUrl, id).replaceAll(" ", "");
    }

    private String getCommentUrl(long commentId, PullRequest pullRequest) {
        return bitbucketProperty.getUrlPullRequestComment()
                .replace("{projectKey}", pullRequest.getProjectKey())
                .replace("{repositorySlug}", pullRequest.getRepositorySlug())
                .replace("{pullRequestId}", pullRequest.getBitbucketId().toString())
                .replace("{commentId}", String.valueOf(commentId));
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
            changeService.save(
                    AnswerCommentChange.builder()
                            .telegramIds(
                                    personService.getAllTelegramIdByLogin(Collections.singleton(newComment.getAuthor()))
                            )
                            .url(newComment.getPullRequestId().toString())
                            .youMessage(newComment.getMessage())
                            .answers(
                                    newAnswers.stream()
                                            .map(answerComment -> Answer.of(answerComment.getAuthor(), answerComment.getMessage()))
                                            .collect(Collectors.toList())
                            )
                            .build()
            );
        }
    }

}
