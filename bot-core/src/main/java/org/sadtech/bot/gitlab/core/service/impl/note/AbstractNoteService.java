package org.sadtech.bot.gitlab.core.service.impl.note;

import lombok.NonNull;
import org.sadtech.bot.gitlab.context.domain.PersonInformation;
import org.sadtech.bot.gitlab.context.domain.entity.Note;
import org.sadtech.bot.gitlab.context.domain.notify.comment.CommentNotify;
import org.sadtech.bot.gitlab.context.service.NotifyService;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;
import org.sadtech.haiti.core.service.AbstractSimpleManagerService;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * // TODO: 17.01.2021 Добавить описание.
 *
 * @author upagge 17.01.2021
 */
public abstract class AbstractNoteService<T extends Note> extends AbstractSimpleManagerService<T, Long> {

    protected static final Pattern PATTERN = Pattern.compile("@[\\w]+");

    private final NotifyService notifyService;
    private final PersonInformation personInformation;

    protected AbstractNoteService(
            SimpleManagerRepository<T, Long> repository,
            NotifyService notifyService,
            PersonInformation personInformation
    ) {
        super(repository);
        this.notifyService = notifyService;
        this.personInformation = personInformation;
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
