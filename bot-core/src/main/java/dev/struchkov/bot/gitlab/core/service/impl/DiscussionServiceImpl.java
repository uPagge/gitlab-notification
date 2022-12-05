package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.context.domain.notify.comment.CommentNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.task.TaskCloseNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.task.TaskNewNotify;
import dev.struchkov.bot.gitlab.context.repository.DiscussionRepository;
import dev.struchkov.bot.gitlab.context.service.DiscussionService;
import dev.struchkov.bot.gitlab.context.service.NotifyService;
import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.bot.gitlab.core.config.properties.PersonProperty;
import dev.struchkov.bot.gitlab.core.utils.StringUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.context.exception.NotFoundException.notFoundException;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static java.lang.Boolean.FALSE;

/**
 * Сервис для работы с дискуссиями.
 *
 * @author upagge 11.02.2021
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiscussionServiceImpl implements DiscussionService {

    protected static final Pattern PATTERN = Pattern.compile("@[\\w]+");

    private final DiscussionRepository repository;
    private final PersonInformation personInformation;

    private final OkHttpClient client = new OkHttpClient();
    private final GitlabProperty gitlabProperty;
    private final PersonProperty personProperty;
    private final NotifyService notifyService;

    @Override
    @Transactional
    public Discussion create(@NonNull Discussion discussion) {
        final List<Note> notes = discussion.getNotes();

        notes.forEach(this::notificationPersonal);
        notes.forEach(note -> notifyNewNote(note, discussion));

        final boolean resolved = discussion.getNotes().stream()
                .allMatch(note -> note.isResolvable() && note.getResolved());

        discussion.setResolved(resolved);

        return repository.save(discussion);
    }

    /**
     * <p>Уведомляет пользователя, если появился новый комментарий</p>
     */
    private void notifyNewNote(Note note, Discussion discussion) {
        if (isNeedNotifyNewNote(note, discussion)) {
            notifyService.send(
                    TaskNewNotify.builder()
                            .authorName(note.getAuthor().getName())
                            .messageTask(note.getBody())
                            .url(note.getWebUrl())
                            .build()
            );
        }
    }

    private boolean isNeedNotifyNewNote(Note note, Discussion discussion) {
        final Long gitlabUserId = personInformation.getId();
        return note.isResolvable() // Тип комментария требует решения (Задачи)
                && gitlabUserId.equals(discussion.getResponsible().getId()) // Создатель дискуссии пользователь приложения
                && !gitlabUserId.equals(note.getAuthor().getId()) // Создатель комментария не пользователь системы
                && FALSE.equals(note.getResolved()); // Комментарий не отмечен как решенный
    }

    @Override
    public Discussion update(@NonNull Discussion discussion) {
        final Discussion oldDiscussion = repository.findById(discussion.getId())
                .orElseThrow(notFoundException("Дискуссия не найдена"));

        discussion.setResponsible(oldDiscussion.getResponsible());
        discussion.setMergeRequest(oldDiscussion.getMergeRequest());

        final Person responsiblePerson = discussion.getResponsible();
        if (checkNotNull(responsiblePerson)) {
            for (Note note : discussion.getNotes()) {
                if (responsiblePerson.getId().equals(note.getAuthor().getId())) {
                    note.setAuthor(responsiblePerson);
                }
                final Person resolvedBy = note.getResolvedBy();
                if (checkNotNull(resolvedBy)) {
                    if (responsiblePerson.getId().equals(resolvedBy.getId())) {
                        note.setResolvedBy(responsiblePerson);
                    }
                }
            }
        }
        notifyUpdateNote(oldDiscussion, discussion);

        final boolean resolved = discussion.getNotes().stream()
                .allMatch(note -> note.isResolvable() && note.getResolved());

        discussion.setResolved(resolved);

        return repository.save(discussion);
    }

    @Override
    public List<Discussion> updateAll(@NonNull List<Discussion> discussions) {
        return discussions.stream()
                .map(this::update)
                .collect(Collectors.toList());
    }

    private void notifyUpdateNote(Discussion oldDiscussion, Discussion discussion) {
        final Map<Long, Note> noteMap = oldDiscussion
                .getNotes().stream()
                .collect(Collectors.toMap(Note::getId, n -> n));

        // Пользователь участвовал в обсуждении
        final boolean userParticipatedInDiscussion = oldDiscussion.getNotes().stream()
                .anyMatch(note -> personInformation.getId().equals(note.getAuthor().getId()));

        for (Note newNote : discussion.getNotes()) {
            final Long newNoteId = newNote.getId();
            if (noteMap.containsKey(newNoteId)) {
                final Note oldNote = noteMap.get(newNoteId);

                if (newNote.isResolvable()) {
                    updateTask(newNote, oldNote);
                }

            } else {
                if (userParticipatedInDiscussion) {
                    notifyNewAnswer(newNote);
                } else {
                    notificationPersonal(newNote);
                }
            }
        }

    }

    private void notifyNewAnswer(Note note) {
        if (!personInformation.getId().equals(note.getAuthor().getId())) {
            notifyService.send(
                    CommentNotify.builder()
                            .url(note.getWebUrl())
                            .message(note.getBody())
                            .authorName(note.getAuthor().getName())
                            .build()
            );
        }
    }

    private void updateTask(Note note, Note oldNote) {
        if (isResolved(note, oldNote)) {
            final MergeRequest mergeRequest = oldNote.getDiscussion().getMergeRequest();
            final List<Discussion> discussions = getAllByMergeRequestId(mergeRequest.getId())
                    .stream()
                    .filter(discussion -> Objects.nonNull(discussion.getResponsible()))
                    .toList();
            final long allYouTasks = discussions.stream()
                    .filter(discussion -> personInformation.getId().equals(discussion.getFirstNote().getAuthor().getId()))
                    .count();
            final long resolvedYouTask = discussions.stream()
                    .filter(discussion -> personInformation.getId().equals(discussion.getFirstNote().getAuthor().getId()) && discussion.getResolved())
                    .count();
            notifyService.send(
                    TaskCloseNotify.builder()
                            .authorName(oldNote.getAuthor().getName())
                            .messageTask(oldNote.getBody())
                            .url(oldNote.getWebUrl())
                            .personTasks(allYouTasks)
                            .personResolvedTasks(resolvedYouTask)
                            .build()
            );
        }
    }

    private boolean isResolved(Note note, Note oldNote) {
        return oldNote.getResolvedBy() == null
                && note.getResolvedBy() != null
                && personInformation.getId().equals(oldNote.getAuthor().getId())
                && !note.getResolvedBy().getId().equals(oldNote.getAuthor().getId());
    }


    @Override
    public void answer(@NonNull String discussionId, @NonNull String text) {
        final Discussion discussion = repository.findById(discussionId)
                .orElseThrow(notFoundException("Дисскусия {0} не найдена", discussionId));
        final MergeRequest mergeRequest = discussion.getMergeRequest();
        final Long projectId = mergeRequest.getProjectId();

        final String requestUrl = MessageFormat.format(gitlabProperty.getUrlNewNote(), projectId, mergeRequest.getTwoId(), discussion.getId(), text);

        RequestBody formBody = new FormBody.Builder().build();

        Request request = new Request.Builder()
                .post(formBody)
                .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                .url(requestUrl)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    public List<Discussion> getAllByMergeRequestId(@NonNull Long mergeRequestId) {
        return repository.findAllByMergeRequestId(mergeRequestId);
    }

    @Override
    public ExistContainer<Discussion, String> existsById(@NonNull Set<String> discussionIds) {
        final List<Discussion> existsEntity = repository.findAllById(discussionIds);
        final Set<String> existsIds = existsEntity.stream().map(Discussion::getId).collect(Collectors.toSet());
        if (existsIds.containsAll(discussionIds)) {
            return ExistContainer.allFind(existsEntity);
        } else {
            final Set<String> noExistsId = discussionIds.stream()
                    .filter(id -> !existsIds.contains(id))
                    .collect(Collectors.toSet());
            return ExistContainer.notAllFind(existsEntity, noExistsId);
        }
    }

    @Override
    public List<Discussion> createAll(@NonNull List<Discussion> newDiscussions) {
        return newDiscussions.stream()
                .map(this::create)
                .toList();
    }

    @Override
    public Page<Discussion> getAll(@NonNull Pageable pagination) {
        return repository.findAll(pagination);
    }

    @Override
    public void deleteById(String discussionId) {
        repository.deleteById(discussionId);
    }

    /**
     * Уведомляет пользователя, если его никнейм упоминается в комментарии.
     */
    protected void notificationPersonal(@NonNull Note note) {
        final Matcher matcher = PATTERN.matcher(note.getBody());
        final Set<String> recipientsLogins = new HashSet<>();
        while (matcher.find()) {
            final String login = matcher.group(0).replace("@", "");
            recipientsLogins.add(login);
        }
        if (recipientsLogins.contains(personInformation.getUsername())) {
            notifyService.send(
                    CommentNotify.builder()
                            .authorName(note.getAuthor().getName())
                            .message(note.getBody())
                            .url(note.getWebUrl())
                            .build()
            );
        }
    }

}
