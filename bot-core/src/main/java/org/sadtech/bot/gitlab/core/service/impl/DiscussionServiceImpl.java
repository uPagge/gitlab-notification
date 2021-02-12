package org.sadtech.bot.gitlab.core.service.impl;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.sadtech.bot.gitlab.context.domain.PersonInformation;
import org.sadtech.bot.gitlab.context.domain.entity.Discussion;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.bot.gitlab.context.domain.notify.comment.CommentNotify;
import org.sadtech.bot.gitlab.context.repository.DiscussionRepository;
import org.sadtech.bot.gitlab.context.service.DiscussionService;
import org.sadtech.bot.gitlab.context.service.NotifyService;
import org.sadtech.bot.gitlab.core.config.properties.GitlabProperty;
import org.sadtech.bot.gitlab.core.config.properties.PersonProperty;
import org.sadtech.haiti.context.exception.NotFoundException;
import org.sadtech.haiti.core.service.AbstractSimpleManagerService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.sadtech.haiti.utils.network.HttpParse.AUTHORIZATION;
import static org.sadtech.haiti.utils.network.HttpParse.BEARER;

/**
 * // TODO: 11.02.2021 Добавить описание.
 *
 * @author upagge 11.02.2021
 */
@Slf4j
@Service
public class DiscussionServiceImpl extends AbstractSimpleManagerService<Discussion, String> implements DiscussionService {

    protected static final Pattern PATTERN = Pattern.compile("@[\\w]+");

    private final DiscussionRepository discussionRepository;
    private final PersonInformation personInformation;

    private final OkHttpClient client = new OkHttpClient();
    private final GitlabProperty gitlabProperty;
    private final PersonProperty personProperty;
    private final NotifyService notifyService;

    public DiscussionServiceImpl(DiscussionRepository discussionRepository, PersonInformation personInformation, GitlabProperty gitlabProperty, PersonProperty personProperty, NotifyService notifyService) {
        super(discussionRepository);
        this.discussionRepository = discussionRepository;
        this.personInformation = personInformation;
        this.gitlabProperty = gitlabProperty;
        this.personProperty = personProperty;
        this.notifyService = notifyService;
    }

    @Override
    public Discussion create(@NonNull Discussion discussion) {
        discussion.getNotes().forEach(this::notificationPersonal);
        return discussionRepository.save(discussion);
    }

    @Override
    public Discussion update(@NonNull Discussion discussion) {
        final Discussion oldDiscussion = discussionRepository.findById(discussion.getId()).orElseThrow(() -> new NotFoundException("Дискуссия не найдена"));
        final Map<Long, Note> noteMap = oldDiscussion
                .getNotes().stream()
                .collect(Collectors.toMap(Note::getId, note -> note));

        discussion.setMergeRequest(oldDiscussion.getMergeRequest());
        discussion.setResponsible(oldDiscussion.getResponsible());
        discussion.getNotes().forEach(note -> updateNote(note, noteMap));

        return discussionRepository.save(discussion);
    }

    private void updateNote(Note note, Map<Long, Note> noteMap) {
        if (noteMap.containsKey(note.getId())) {
            final Note oldNote = noteMap.get(note.getId());
            note.setWebUrl(oldNote.getWebUrl());
        } else {
            notificationPersonal(note);
        }
    }


    @Override
    public void answer(@NonNull String discussionId, @NonNull String text) {
        final Discussion discussion = discussionRepository.findById(discussionId)
                .orElseThrow(() -> new org.sadtech.haiti.context.exception.NotFoundException("Дисскусия " + discussionId + " не найдена"));
        final MergeRequest mergeRequest = discussion.getMergeRequest();
        final Long projectId = mergeRequest.getProjectId();

        final String requestUrl = MessageFormat.format(gitlabProperty.getUrlNewNote(), projectId, mergeRequest.getTwoId(), discussion.getId(), text);

        RequestBody formBody = new FormBody.Builder().build();

        Request request = new Request.Builder()
                .post(formBody)
                .header(AUTHORIZATION, BEARER + personProperty.getToken())
                .url(requestUrl)
                .build();

        try {
            final Response execute = client.newCall(request).execute();
            System.out.println(execute.isSuccessful());
            System.out.println(execute.body().toString());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }

    protected void notificationPersonal(@NonNull Note note) {
        Matcher matcher = PATTERN.matcher(note.getBody());
        Set<String> recipientsLogins = new HashSet<>();
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
