package dev.struchkov.bot.gitlab.core.service.impl;

import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequestForDiscussion;
import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.context.domain.notify.comment.NewCommentNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.level.DiscussionLevel;
import dev.struchkov.bot.gitlab.context.domain.notify.task.DiscussionNewNotify;
import dev.struchkov.bot.gitlab.context.domain.notify.task.ThreadCloseNotify;
import dev.struchkov.bot.gitlab.context.repository.DiscussionRepository;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.service.DiscussionService;
import dev.struchkov.bot.gitlab.context.service.NotifyService;
import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.bot.gitlab.core.config.properties.PersonProperty;
import dev.struchkov.bot.gitlab.core.utils.StringUtils;
import dev.struchkov.haiti.utils.container.Pair;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static dev.struchkov.bot.gitlab.context.domain.notify.level.DiscussionLevel.NOTIFY_WITH_CONTEXT;
import static dev.struchkov.bot.gitlab.context.domain.notify.level.DiscussionLevel.WITHOUT_NOTIFY;
import static dev.struchkov.haiti.context.exception.NotFoundException.notFoundException;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.Checker.checkNull;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

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
    private final OkHttpClient client = new OkHttpClient();

    private final DiscussionRepository repository;

    private final NotifyService notifyService;
    private final AppSettingService settingService;

    private final PersonInformation personInformation;
    private final GitlabProperty gitlabProperty;
    private final PersonProperty personProperty;

    @Override
    @Transactional
    public Discussion create(@NonNull Discussion discussion) {
        final List<Note> notes = discussion.getNotes();

        final DiscussionLevel levelDiscussionNotify = settingService.getLevelDiscussionNotify();
        if (!WITHOUT_NOTIFY.equals(levelDiscussionNotify)) {
            discussion.setNotification(true);

            if (isNeedNotifyNewNote(discussion)) {
                notifyNewThread(discussion);
            } else {
                notes.forEach(note -> notifyAboutPersonalAnswer(discussion, note));
            }
        } else {
            discussion.setNotification(false);
        }

        final boolean resolved = discussion.getNotes().stream()
                .allMatch(note -> note.isResolvable() && note.getResolved());

        discussion.setResolved(resolved);

        return repository.save(discussion);
    }

    @Override
    @Transactional
    public Discussion update(@NonNull Discussion discussion) {
        final Discussion oldDiscussion = repository.findById(discussion.getId())
                .orElseThrow(notFoundException("Дискуссия не найдена"));

        discussion.setResponsible(oldDiscussion.getResponsible());
        discussion.setMergeRequest(oldDiscussion.getMergeRequest());
        discussion.setNotification(oldDiscussion.isNotification());

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

        final boolean resolved = discussion.getNotes().stream()
                .allMatch(note -> note.isResolvable() && note.getResolved());
        discussion.setResolved(resolved);

        if (oldDiscussion.isNotification()) {
            notifyUpdateNote(oldDiscussion, discussion);
        }


        return repository.save(discussion);
    }

    private boolean isNeedNotifyNewNote(Discussion discussion) {
        final Note firstNote = discussion.getFirstNote();
        final Long gitlabUserId = personInformation.getId();
        return firstNote.isResolvable() // Тип комментария требует решения (Задачи)
               && gitlabUserId.equals(discussion.getResponsible().getId()) // Ответственный за дискуссию пользователь
               && !gitlabUserId.equals(firstNote.getAuthor().getId()) // Создатель комментария не пользователь системы
               && FALSE.equals(firstNote.getResolved()); // Комментарий не отмечен как решенный
    }

    @Override
    public List<Discussion> updateAll(@NonNull List<Discussion> discussions) {
        return discussions.stream()
                .map(this::update)
                .collect(Collectors.toList());
    }

    private void notifyUpdateNote(Discussion oldDiscussion, Discussion discussion) {
        final Map<Long, Note> oldNoteMap = oldDiscussion
                .getNotes().stream()
                .collect(Collectors.toMap(Note::getId, n -> n));

        // Пользователь участвовал в обсуждении
        final boolean userParticipatedInDiscussion = oldDiscussion.getNotes().stream()
                .anyMatch(note -> personInformation.getId().equals(note.getAuthor().getId()));

        final Note threadFirstNote = discussion.getFirstNote();
        if (TRUE.equals(discussion.getResolved())) {
            notifyAboutCloseThread(threadFirstNote, oldNoteMap.get(threadFirstNote.getId()), discussion.getLastNote());
        }

        for (Note newNote : discussion.getNotes()) {
            final Long newNoteId = newNote.getId();
            if (!oldNoteMap.containsKey(newNoteId)) {
                if (userParticipatedInDiscussion) {
                    notifyAboutNewAnswer(discussion, newNote);
                } else {
                    notifyAboutPersonalAnswer(discussion, newNote);
                }
            }
        }

    }

    private void notifyAboutCloseThread(Note newNote, Note oldNote, Optional<Note> lastNote) {
        final DiscussionLevel level = settingService.getLevelDiscussionNotify();
        if (!WITHOUT_NOTIFY.equals(level)) {

            if (isResolved(newNote, oldNote)) {
                final MergeRequestForDiscussion mergeRequest = oldNote.getDiscussion().getMergeRequest();

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

                final ThreadCloseNotify.ThreadCloseNotifyBuilder notifyBuilder = ThreadCloseNotify.builder()
                        .mergeRequestName(mergeRequest.getTitle())
                        .url(oldNote.getWebUrl())
                        .personTasks(allYouTasks)
                        .personResolvedTasks(resolvedYouTask);

                if (NOTIFY_WITH_CONTEXT.equals(level)) {
                    notifyBuilder
                            .authorName(oldNote.getAuthor().getName())
                            .messageTask(oldNote.getBody());


                    lastNote.ifPresent(
                            note -> {
                                notifyBuilder.authorLastNote(note.getAuthor().getName());
                                notifyBuilder.messageLastNote(note.getBody());
                            }
                    );
                }

                notifyService.send(notifyBuilder.build());
            }

        }
    }

    private boolean isResolved(Note note, Note oldNote) {
        return checkNull(oldNote.getResolvedBy()) // В старом комментарии не было отметки о решении
               && checkNotNull(note.getResolvedBy()) // А в новом есть отметка
               && personInformation.getId().equals(oldNote.getAuthor().getId()) // и решающий не является пользователем бота
               && !note.getResolvedBy().getId().equals(oldNote.getAuthor().getId()); // и решающий не является автором треда
    }


    @Override
    public void answer(@NonNull String discussionId, @NonNull String text) {
        final Discussion discussion = repository.findById(discussionId)
                .orElseThrow(notFoundException("Дисскусия {0} не найдена", discussionId));
        final MergeRequestForDiscussion mergeRequest = discussion.getMergeRequest();
        final Long projectId = mergeRequest.getProjectId();

        final String requestUrl = MessageFormat.format(gitlabProperty.getNewNoteUrl(), projectId, mergeRequest.getTwoId(), discussion.getId(), text);

        final RequestBody formBody = new FormBody.Builder().build();

        final Request request = new Request.Builder()
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
    public List<Discussion> getAll() {
        return repository.findAll();
    }

    @Override
    public Set<String> getAllIds() {
        return repository.findAllIds();
    }

    @Override
    @Transactional
    public void deleteById(@NonNull String discussionId) {
        repository.deleteById(discussionId);
    }

    @Override
    @Transactional
    public void cleanOld() {
        log.debug("Старт очистки старых дискуссий");
        repository.cleanOld();
        log.debug("Конец очистки старых дискуссий");
    }

    @Override
    @Transactional
    public void notification(boolean enable, String discussionId) {
        repository.notification(enable, discussionId);
    }

    private void notifyAboutNewAnswer(Discussion discussion, Note note) {
        final DiscussionLevel discussionLevel = settingService.getLevelDiscussionNotify();

        if (!WITHOUT_NOTIFY.equals(discussionLevel)
            && !personInformation.getId().equals(note.getAuthor().getId())) {
            final Note firstNote = discussion.getFirstNote();

            final NewCommentNotify.NewCommentNotifyBuilder notifyBuilder = NewCommentNotify.builder()
                    .threadId(discussion.getId())
                    .url(note.getWebUrl())
                    .mergeRequestName(discussion.getMergeRequest().getTitle());

            if (NOTIFY_WITH_CONTEXT.equals(discussionLevel)) {
                final Optional<Note> prevLastNote = discussion.getPrevLastNote();

                if (prevLastNote.isPresent()) {
                    final Note prevNote = prevLastNote.get();
                    notifyBuilder.previousMessage(prevNote.getBody());
                    notifyBuilder.previousAuthor(prevNote.getAuthor().getName());
                }

                notifyBuilder
                        .discussionMessage(firstNote.getBody())
                        .discussionAuthor(firstNote.getAuthor().getName())
                        .message(note.getBody())
                        .authorName(note.getAuthor().getName());
            }

            notifyService.send(notifyBuilder.build());
        }
    }

    /**
     * Уведомляет пользователя, если его никнейм упоминается в комментарии.
     */
    private void notifyAboutPersonalAnswer(Discussion discussion, Note note) {
        final DiscussionLevel discussionLevel = settingService.getLevelDiscussionNotify();
        if (!WITHOUT_NOTIFY.equals(discussionLevel)) {
            final Matcher matcher = PATTERN.matcher(note.getBody());
            final Set<String> recipientsLogins = new HashSet<>();

            while (matcher.find()) {
                final String login = matcher.group(0).replace("@", "");
                recipientsLogins.add(login);
            }

            if (recipientsLogins.contains(personInformation.getUsername())) {
                final NewCommentNotify.NewCommentNotifyBuilder notifyBuilder = NewCommentNotify.builder()
                        .threadId(discussion.getId())
                        .mergeRequestName(discussion.getMergeRequest().getTitle())
                        .url(note.getWebUrl());

                if (NOTIFY_WITH_CONTEXT.equals(discussionLevel)) {
                    final Optional<Note> prevLastNote = discussion.getPrevLastNote();
                    final Note firstNote = discussion.getFirstNote();

                    if (!firstNote.equals(note)) {
                        notifyBuilder.message(note.getBody())
                                .authorName(note.getAuthor().getName());
                    }
                    if (prevLastNote.isPresent()) {
                        final Note prevNote = prevLastNote.get();
                        notifyBuilder.previousMessage(prevNote.getBody());
                        notifyBuilder.previousAuthor(prevNote.getAuthor().getName());
                    }

                    notifyBuilder
                            .discussionMessage(firstNote.getBody())
                            .discussionAuthor(firstNote.getAuthor().getName());
                }

                notifyService.send(notifyBuilder.build());
            }
        }
    }

    /**
     * <p>Уведомляет пользователя, если появился новый комментарий</p>
     */
    private void notifyNewThread(Discussion discussion) {
        final DiscussionLevel discussionLevel = settingService.getLevelDiscussionNotify();
        if (!WITHOUT_NOTIFY.equals(discussionLevel)) {
            final Note firstNote = discussion.getFirstNote();

            final MergeRequestForDiscussion mergeRequest = discussion.getMergeRequest();
            final DiscussionNewNotify.DiscussionNewNotifyBuilder messageBuilder = DiscussionNewNotify.builder()
                    .url(firstNote.getWebUrl())
                    .threadId(discussion.getId())
                    .mergeRequestName(mergeRequest.getTitle())
                    .authorName(firstNote.getAuthor().getName());

            if (NOTIFY_WITH_CONTEXT.equals(discussionLevel)) {
                final List<Note> notes = discussion.getNotes();

                messageBuilder
                        .discussionMessage(firstNote.getBody());

                if (notes.size() > 1) {
                    for (int i = 1; i < notes.size(); i++) {
                        final Note note = notes.get(i);
                        messageBuilder.note(
                                new Pair<>(note.getAuthor().getName(), note.getBody())
                        );
                    }
                }
            }

            notifyService.send(messageBuilder.build());
        }
    }

}
