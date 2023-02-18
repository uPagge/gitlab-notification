package dev.struchkov.bot.gitlab.telegram.unit.command;

import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.service.DiscussionService;
import dev.struchkov.bot.gitlab.context.service.NoteService;
import dev.struchkov.godfather.main.domain.annotation.Unit;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.domain.unit.AnswerText;
import dev.struchkov.godfather.telegram.domain.attachment.LinkAttachment;
import dev.struchkov.godfather.telegram.main.core.util.Attachments;
import dev.struchkov.godfather.telegram.starter.UnitConfiguration;
import dev.struchkov.haiti.utils.Checker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.ANSWER_NOTE;
import static dev.struchkov.godfather.simple.domain.BoxAnswer.boxAnswer;

@Component
@RequiredArgsConstructor
public class AnswerNoteUnit implements UnitConfiguration {

    private static final Pattern NOTE_LINK = Pattern.compile("#note_\\d+$");

    private final PersonInformation personInformation;
    private final AppSettingService settingService;
    private final NoteService noteService;
    private final DiscussionService discussionService;

    //TODO [07.02.2023|uPagge]: Можно возвращать ссылку на ответ
    @Unit(value = ANSWER_NOTE, global = true)
    public AnswerText<Mail> answerNote() {
        return AnswerText.<Mail>builder()
                .triggerCheck(
                        mail -> {
                            final List<Mail> forwardMails = mail.getForwardMail();
                            if (Checker.checkNotNull(forwardMails) && forwardMails.size() == 1) {
                                final Mail forwardMail = forwardMails.get(0);
                                final boolean isLink = Attachments.findFirstLink(forwardMail.getAttachments())
                                        .isPresent();
                                if (isLink) {
                                    final boolean isAccess = personInformation.getTelegramId().equals(mail.getPersonId());
                                    final boolean firstStart = settingService.isFirstStart();
                                    return isAccess && !firstStart;
                                }
                            }
                            return false;
                        }
                )
                .answer(
                        mail -> {
                            final String noteUrl = Attachments.findFirstLink(mail.getForwardMail().get(0).getAttachments())
                                    .map(LinkAttachment::getUrl)
                                    .orElseThrow();
                            final Matcher matcher = NOTE_LINK.matcher(noteUrl);
                            if (matcher.find()) {
                                final String noteText = noteUrl.substring(matcher.start(), matcher.end());
                                final Long noteId = Long.valueOf(noteText.replace("#note_", ""));
                                final Note note = noteService.getByIdOrThrow(noteId);
                                final String discussionId = note.getDiscussion().getId();
                                discussionService.answer(discussionId, MessageFormat.format("@{0}, {1}", note.getAuthor().getUserName(), mail.getText()));
                                return boxAnswer(
                                        "\uD83D\uDC4D Response sent successfully"
                                );
                            }
                            return boxAnswer("Error");
                        }
                )
                .build();
    }

}
