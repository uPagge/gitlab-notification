package org.sadtech.bot.gitlab.core.service.parser;

import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.bot.gitlab.context.domain.entity.Task;
import org.sadtech.bot.gitlab.context.service.MergeRequestsService;
import org.sadtech.bot.gitlab.context.service.NoteService;
import org.sadtech.bot.gitlab.context.service.TaskService;
import org.sadtech.bot.gitlab.core.config.properties.GitlabProperty;
import org.sadtech.bot.gitlab.core.config.properties.PersonProperty;
import org.sadtech.bot.gitlab.sdk.domain.NoteJson;
import org.sadtech.haiti.context.domain.ExistsContainer;
import org.sadtech.haiti.context.exception.ConvertException;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.core.page.PaginationImpl;
import org.sadtech.haiti.utils.network.HttpParse;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.sadtech.haiti.utils.network.HttpParse.ACCEPT;
import static org.sadtech.haiti.utils.network.HttpParse.AUTHORIZATION;
import static org.sadtech.haiti.utils.network.HttpParse.BEARER;

/**
 * <p>Поиск новых комментариев и задач.</p>
 * <p>К несчастью, у битбакета не очень удобный API, и у них таска это то же самое что и комментарий, только с флагом</p>
 */
@Component
public class NoteParser {

    public static final int COUNT = 100;

    private final MergeRequestsService mergeRequestsService;
    private final ConversionService conversionService;

    private final GitlabProperty gitlabProperty;
    private final PersonProperty personProperty;
    private final NoteService noteService;
    private final TaskService taskService;

    public NoteParser(
            MergeRequestsService mergeRequestsService,
            ConversionService conversionService,
            GitlabProperty gitlabProperty,
            PersonProperty personProperty,
            NoteService noteService,
            TaskService taskService
    ) {
        this.mergeRequestsService = mergeRequestsService;
        this.conversionService = conversionService;
        this.gitlabProperty = gitlabProperty;
        this.personProperty = personProperty;
        this.noteService = noteService;
        this.taskService = taskService;
    }

    public void scanNewCommentAndTask() {
        int page = 0;
        Sheet<MergeRequest> mergeRequestSheet = mergeRequestsService.getAll(PaginationImpl.of(page, COUNT));

        while (mergeRequestSheet.hasContent()) {

            final List<MergeRequest> mergeRequests = mergeRequestSheet.getContent();
            for (MergeRequest mergeRequest : mergeRequests) {

                processingMergeRequest(mergeRequest);

            }

            mergeRequestSheet = mergeRequestsService.getAll(PaginationImpl.of(++page, COUNT));
        }

    }

    private void processingMergeRequest(MergeRequest mergeRequest) {
        int page = 1;
        List<NoteJson> noteJsons = getNoteJson(mergeRequest, page);

        while (!noteJsons.isEmpty()) {

            createNewComment(noteJsons, mergeRequest);
            createNewTask(noteJsons, mergeRequest);

            noteJsons = getNoteJson(mergeRequest, ++page);
        }
    }

    private List<NoteJson> getNoteJson(MergeRequest mergeRequest, int page) {
        return HttpParse.request(MessageFormat.format(gitlabProperty.getUrlPullRequestComment(), mergeRequest.getProjectId(), mergeRequest.getTwoId(), page))
                .header(ACCEPT)
                .header(AUTHORIZATION, BEARER + personProperty.getToken())
                .executeList(NoteJson.class)
                .stream()
                .filter(noteJson -> !noteJson.isSystem())
                .collect(Collectors.toList());
    }

    private void createNewTask(List<NoteJson> noteJsons, MergeRequest mergeRequest) {
        final List<NoteJson> newJsons = noteJsons.stream()
                .filter(json -> json.getType() != null)
                .collect(Collectors.toList());

        final Set<Long> jsonIds = newJsons.stream()
                .map(NoteJson::getId)
                .collect(Collectors.toSet());

        final ExistsContainer<Task, Long> existsContainer = taskService.existsById(jsonIds);

        if (!existsContainer.isAllFound()) {
            final List<Task> newNotes = newJsons.stream()
                    .filter(json -> existsContainer.getIdNoFound().contains(json.getId()))
                    .map(json -> conversionService.convert(json, Task.class))
                    .peek(task -> {
                                task.setWebUrl(MessageFormat.format(gitlabProperty.getUrlNote(), mergeRequest.getWebUrl(), task.getId()));
                                task.setResponsible(mergeRequest.getAuthor());
                            }
                    )
                    .collect(Collectors.toList());

            final List<Task> newTasks = taskService.createAll(newNotes);
            newTasks.forEach(task -> noteService.link(task.getId(), mergeRequest.getId()));
        }
    }

    private void createNewComment(List<NoteJson> noteJsons, MergeRequest mergeRequest) {
        final List<NoteJson> newJsons = noteJsons.stream()
                .filter(json -> json.getType() == null)
                .collect(Collectors.toList());

        final Set<Long> jsonIds = newJsons.stream()
                .map(NoteJson::getId)
                .collect(Collectors.toSet());

        final ExistsContainer<Note, Long> existsContainer = noteService.existsById(jsonIds);

        if (!existsContainer.isAllFound()) {
            final List<Note> notes = newJsons.stream()
                    .filter(json -> existsContainer.getIdNoFound().contains(json.getId()))
                    .map(json -> conversionService.convert(json, Note.class))
                    .peek(note -> note.setWebUrl(
                            MessageFormat.format(gitlabProperty.getUrlNote(), mergeRequest.getWebUrl(), note.getId()))
                    )
                    .collect(Collectors.toList());

            final List<Note> newNotes = noteService.createAll(notes);
            newNotes.forEach(note -> noteService.link(note.getId(), mergeRequest.getId()));
        }

    }

    public void scanOldTask() {
        int page = 0;
        Sheet<Task> taskSheet = taskService.getAllByResolved(false, PaginationImpl.of(page, COUNT));

        while (taskSheet.hasContent()) {
            final List<Task> tasks = taskSheet.getContent();

            for (Task task : tasks) {
                final MergeRequest mergeRequest = task.getMergeRequest();
                final Task newTask = HttpParse.request(MessageFormat.format(
                        gitlabProperty.getUrlNoteApi(),
                        mergeRequest.getProjectId(),
                        mergeRequest.getTwoId(),
                        task.getId())
                )
                        .header(ACCEPT)
                        .header(AUTHORIZATION, BEARER + personProperty.getToken())
                        .execute(NoteJson.class)
                        .map(json -> conversionService.convert(json, Task.class))
                        .orElseThrow(() -> new ConvertException("Ошибка обработки задачи"));
                taskService.update(newTask);
            }

            taskSheet = taskService.getAllByResolved(false, PaginationImpl.of(++page, COUNT));
        }

    }

//    private List<DataScan> generatingLinksToPossibleComments(@NonNull Long commentId) {
//        List<DataScan> commentUrls = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            int page = 0;
//            Sheet<MergeRequest> pullRequestPage = mergeRequestsService.getAll(
//                    PaginationImpl.of(page, commentSchedulerProperty.getCommentCount())
//            );
//            while (pullRequestPage.hasContent()) {
//                long finalCommentId = commentId;
//                commentUrls.addAll(pullRequestPage.getContent().stream()
//                        .map(
//                                pullRequest -> new DataScan(
//                                        getCommentUrl(finalCommentId, pullRequest),
//                                        pullRequest.getId()
//                                )
//                        )
//                        .collect(Collectors.toList()));
//                pullRequestPage = mergeRequestsService.getAll(
//                        PaginationImpl.of(++page, commentSchedulerProperty.getCommentCount())
//                );
//            }
//            commentId++;
//        }
//        return commentUrls;
//    }

//    private List<Note> getCommentsByResultScan(List<NoteJson> noteJsons) {
//        return noteJsons.stream()
//                .filter(json -> Severity.NORMAL.equals(json.getSeverity()))
//                .map(resultScan -> conversionService.convert(resultScan, Note.class))
//                .peek(
//                        comment -> {
//                            final MergeRequestMini mergeRequestMini = mergeRequestsService.getMiniInfo(comment.getPullRequestId())
//                                    .orElseThrow(() -> new NotFoundException("Автор ПР не найден"));
//                            comment.setUrl(generateUrl(comment.getId(), mergeRequestMini.getWebUrl()));
//                            comment.setResponsible(mergeRequestMini.getAuthor());
//                        }
//                )
//                .collect(Collectors.toList());
//    }

//    private List<Task> getTaskByResultScan(List<NoteJson> noteJsons) {
//        return noteJsons.stream()
//                .filter(json -> Severity.BLOCKER.equals(json.getSeverity()))
//                .map(resultScan -> conversionService.convert(resultScan, Task.class))
//                .peek(
//                        task -> {
//                            final MergeRequestMini mergeRequestMini = mergeRequestsService.getMiniInfo(task.getPullRequestId())
//                                    .orElseThrow(() -> new NotFoundException("Автор ПР не найден"));
//                            task.setResponsible(mergeRequestMini.getAuthorLogin());
//                            task.setUrl(generateUrl(task.getId(), mergeRequestMini.getWebUrl()));
//                        }
//                )
//                .collect(Collectors.toList());
//    }

}
