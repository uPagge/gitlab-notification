package org.sadtech.bot.gitlab.core.service.parser;

import org.springframework.stereotype.Component;

/**
 * <p>Поиск новых комментариев и задач.</p>
 * <p>К несчастью, у битбакета не очень удобный API, и у них таска это то же самое что и комментарий, только с флагом</p>
 */
@Component
public class NoteParser {

//    public static final int COUNT = 100;
//
//    private final MergeRequestsService mergeRequestsService;
//    private final ConversionService conversionService;
//
//    private final GitlabProperty gitlabProperty;
//    private final PersonProperty personProperty;
//    private final NoteService noteService;
//
//    public NoteParser(
//            MergeRequestsService mergeRequestsService,
//            ConversionService conversionService,
//            GitlabProperty gitlabProperty,
//            PersonProperty personProperty,
//            NoteService noteService
//    ) {
//        this.mergeRequestsService = mergeRequestsService;
//        this.conversionService = conversionService;
//        this.gitlabProperty = gitlabProperty;
//        this.personProperty = personProperty;
//        this.noteService = noteService;
//    }
//
//    public void scanNewCommentAndTask() {
//        int page = 0;
//        Sheet<MergeRequest> mergeRequestSheet = mergeRequestsService.getAll(PaginationImpl.of(page, COUNT));
//
//        while (mergeRequestSheet.hasContent()) {
//
//            final List<MergeRequest> mergeRequests = mergeRequestSheet.getContent();
//            for (MergeRequest mergeRequest : mergeRequests) {
//
//                processingMergeRequest(mergeRequest);
//
//            }
//
//            mergeRequestSheet = mergeRequestsService.getAll(PaginationImpl.of(++page, COUNT));
//        }
//
//    }
//
//    private void processingMergeRequest(MergeRequest mergeRequest) {
//        int page = 1;
//        List<NoteJson> noteJsons = getNoteJson(mergeRequest, page);
//
//        while (!noteJsons.isEmpty()) {
//
//            createNewComment(noteJsons, mergeRequest);
//
//            noteJsons = getNoteJson(mergeRequest, ++page);
//        }
//    }
//
//    private List<NoteJson> getNoteJson(MergeRequest mergeRequest, int page) {
//        return HttpParse.request(MessageFormat.format(gitlabProperty.getUrlPullRequestComment(), mergeRequest.getProjectId(), mergeRequest.getTwoId(), page))
//                .header(ACCEPT)
//                .header(AUTHORIZATION, BEARER + personProperty.getToken())
//                .executeList(NoteJson.class)
//                .stream()
//                .filter(noteJson -> !noteJson.isSystem())
//                .collect(Collectors.toList());
//    }

//    private void createNewTask(List<NoteJson> noteJsons, MergeRequest mergeRequest) {
//        final List<NoteJson> newJsons = noteJsons.stream()
//                .filter(json -> json.getType() != null)
//                .collect(Collectors.toList());
//
//        final Set<Long> jsonIds = newJsons.stream()
//                .map(NoteJson::getId)
//                .collect(Collectors.toSet());
//
//        final ExistsContainer<Task, Long> existsContainer = taskService.existsById(jsonIds);
//
//        if (!existsContainer.isAllFound()) {
//            final List<Task> newNotes = newJsons.stream()
//                    .filter(json -> existsContainer.getIdNoFound().contains(json.getId()))
//                    .map(json -> conversionService.convert(json, Task.class))
//                    .peek(task -> {
//                                task.setWebUrl(MessageFormat.format(gitlabProperty.getUrlNote(), mergeRequest.getWebUrl(), task.getId()));
//                                task.setResponsible(mergeRequest.getAuthor());
//                            }
//                    )
//                    .collect(Collectors.toList());
//
//            final Set<Long> newNoteIds = newNotes.stream().map(Task::getId).collect(Collectors.toSet());
//
//            final ExistsContainer<Note, Long> existsNoteContainer = noteService.existsById(newNoteIds);
//
//            if (existsContainer.getContainer() != null && !existsContainer.getContainer().isEmpty()) {
//                noteService.deleteAllById(existsNoteContainer.getContainer().stream().map(Note::getId).collect(Collectors.toSet()));
//            }
//
//            final List<Task> newTasks = taskService.createAll(newNotes);
//            newTasks.forEach(task -> noteService.link(task.getId(), mergeRequest.getId()));
//        }
//    }
//
//    private void createNewComment(List<NoteJson> noteJsons, MergeRequest mergeRequest) {
//        final List<NoteJson> newJsons = noteJsons.stream()
//                .filter(json -> json.getType() == null)
//                .collect(Collectors.toList());
//
//        final Set<Long> jsonIds = newJsons.stream()
//                .map(NoteJson::getId)
//                .collect(Collectors.toSet());
//
//        final ExistsContainer<Note, Long> existsContainer = noteService.existsById(jsonIds);
//
//        if (!existsContainer.isAllFound()) {
//            final List<Note> notes = newJsons.stream()
//                    .filter(json -> existsContainer.getIdNoFound().contains(json.getId()))
//                    .map(json -> conversionService.convert(json, Note.class))
//                    .peek(note -> note.setWebUrl(
//                            MessageFormat.format(gitlabProperty.getUrlNote(), mergeRequest.getWebUrl(), note.getId()))
//                    )
//                    .collect(Collectors.toList());
//
//            final List<Note> newNotes = noteService.createAll(notes);
//            newNotes.forEach(note -> noteService.link(note.getId(), mergeRequest.getId()));
//        }

//    }

//    public void scanOldTask() {
//        int page = 0;
//        Sheet<Task> taskSheet = taskService.getAllByResolved(false, PaginationImpl.of(page, COUNT));
//
//        while (taskSheet.hasContent()) {
//            final List<Task> tasks = taskSheet.getContent();
//
//            for (Task task : tasks) {
//                final MergeRequest mergeRequest = task.getMergeRequest();
//                // FIXME: 11.02.2021 Костыль, исправить в будущем
//                if (mergeRequest!=null) {
//                    final Task newTask = HttpParse.request(MessageFormat.format(
//                            gitlabProperty.getUrlNoteApi(),
//                            mergeRequest.getProjectId(),
//                            mergeRequest.getTwoId(),
//                            task.getId())
//                    )
//                            .header(ACCEPT)
//                            .header(AUTHORIZATION, BEARER + personProperty.getToken())
//                            .execute(NoteJson.class)
//                            .map(json -> conversionService.convert(json, Task.class))
//                            .orElseThrow(() -> new ConvertException("Ошибка обработки задачи id: " + task.getId()));
//                    taskService.update(newTask);
//                } else {
//                    taskService.deleteById(task.getId());
//                }
//            }
//
//            taskSheet = taskService.getAllByResolved(false, PaginationImpl.of(++page, COUNT));
//        }
//
//    }

}
