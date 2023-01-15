package dev.struchkov.bot.gitlab.telegram.unit;

import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.service.DiscussionService;
import dev.struchkov.bot.gitlab.context.service.NoteService;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import dev.struchkov.godfather.main.domain.annotation.Unit;
import dev.struchkov.godfather.main.domain.content.Attachment;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.core.unit.AnswerText;
import dev.struchkov.godfather.telegram.domain.attachment.ButtonClickAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.LinkAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.TelegramAttachmentType;
import dev.struchkov.godfather.telegram.main.core.util.Attachments;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import dev.struchkov.haiti.utils.Checker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.ANSWER_NOTE;
import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;

@Component
@RequiredArgsConstructor
public class CommandUnit {

    private static final Pattern NOTE_LINK = Pattern.compile("#note_\\d+$");

    private final PersonInformation personInformation;
    private final AppSettingService settingService;
    private final NoteService noteService;
    private final DiscussionService discussionService;
    private final TelegramSending telegramSending;

    @Unit(value = ANSWER_NOTE, main = true)
    public AnswerText<Mail> answerNote() {
        return AnswerText.<Mail>builder()
                .triggerCheck(
                        mail -> {
                            final boolean isAccess = personInformation.getTelegramId().equals(mail.getPersonId());
                            if (isAccess) {
                                final boolean isFirstStart = settingService.isFirstStart();
                                if (!isFirstStart) {
                                    final List<Mail> forwardMails = mail.getForwardMail();
                                    if (Checker.checkNotNull(forwardMails) && forwardMails.size() == 1) {
                                        final Mail forwardMail = forwardMails.get(0);
                                        return Attachments.findFirstLink(forwardMail.getAttachments()).isPresent();
                                    }
                                }
                            }
                            return false;
                        }
                )
                .answer(
                        mail -> {
                            final List<Attachment> attachments = mail.getForwardMail().get(0).getAttachments();
                            for (Attachment attachment : attachments) {
                                if (TelegramAttachmentType.LINK.name().equals(attachment.getType())) {
                                    final String url = ((LinkAttachment) attachment).getUrl();
                                    final Matcher matcher = NOTE_LINK.matcher(url);
                                    if (matcher.find()) {
                                        final String noteText = url.substring(matcher.start(), matcher.end());
                                        final Long noteId = Long.valueOf(noteText.replaceAll("#note_", ""));
                                        final Note note = noteService.getByIdOrThrow(noteId);
                                        final String discussionId = note.getDiscussion().getId();
                                        discussionService.answer(discussionId, MessageFormat.format("@{0}, {1}", note.getAuthor().getUserName(), mail.getText()));
                                        return BoxAnswer.builder().build();
                                    }
                                }
                            }
                            return boxAnswer("Error");
                        }
                )
                .build();
    }

    @Unit(value = "DELETE_MESSAGE", main = true)
    public AnswerText<Mail> deleteMessage() {
        return AnswerText.<Mail>builder()
                .triggerCheck(mail -> {
                    final boolean isAccess = personInformation.getTelegramId().equals(mail.getPersonId());
                    if (isAccess) {
                        final boolean isFirstStart = settingService.isFirstStart();
                        if (!isFirstStart) {
                            final Optional<ButtonClickAttachment> optButtonClick = Attachments.findFirstButtonClick(mail.getAttachments());
                            if (optButtonClick.isPresent()) {
                                final ButtonClickAttachment buttonClick = optButtonClick.get();
                                final String rawData = buttonClick.getRawCallBackData();
                                return rawData.equals("DELETE_MESSAGE");
                            }
                        }
                    }
                    return false;
                })
                .answer(mail -> {
                    final ButtonClickAttachment clickButton = Attachments.findFirstButtonClick(mail.getAttachments()).orElseThrow();
                    telegramSending.deleteMessage(mail.getPersonId(), clickButton.getMessageId());
                })
                .build();
    }

}
