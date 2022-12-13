package dev.struchkov.bot.gitlab.telegram.unit;

import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.service.DiscussionService;
import dev.struchkov.bot.gitlab.context.service.NoteService;
import dev.struchkov.bot.gitlab.context.service.NotifyService;
import dev.struchkov.bot.gitlab.context.utils.Smile;
import dev.struchkov.bot.gitlab.core.service.parser.ProjectParser;
import dev.struchkov.godfather.main.core.unit.UnitActiveType;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import dev.struchkov.godfather.main.domain.annotation.Unit;
import dev.struchkov.godfather.main.domain.content.Attachment;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.core.unit.AnswerCheck;
import dev.struchkov.godfather.simple.core.unit.AnswerText;
import dev.struchkov.godfather.simple.core.unit.MainUnit;
import dev.struchkov.godfather.telegram.domain.attachment.LinkAttachment;
import dev.struchkov.godfather.telegram.domain.attachment.TelegramAttachmentType;
import dev.struchkov.godfather.telegram.main.core.util.Attachments;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.ACCESS_ERROR;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.ANSWER_NOTE;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.AUTHORIZATION;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.CHECK_FIRST_START;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.CHECK_MENU_OR_ANSWER;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.CHECK_PARSER_PRIVATE_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.CHECK_PARSE_OWNER_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.END_SETTING;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.GENERAL_MENU;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.PARSER_PRIVATE_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.PARSE_OWNER_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_PARSER_PRIVATE_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_PARSE_OWNER_PROJECT;
import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.godfather.main.domain.BoxAnswer.replaceBoxAnswer;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine.simpleLine;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Component
@RequiredArgsConstructor
public class UnitConfig {

    private static final Pattern NOTE_LINK = Pattern.compile("#note_\\d+$");

    private final PersonInformation personInformation;

    private final AppSettingService settingService;
    private final NoteService noteService;
    private final DiscussionService discussionService;
    private final NotifyService notifyService;

    private final ProjectParser projectParser;

    @Unit(value = AUTHORIZATION, main = true)
    public AnswerCheck<Mail> auth(
            @Unit(CHECK_FIRST_START) MainUnit<Mail> checkFirstStart,
            @Unit(ACCESS_ERROR) MainUnit<Mail> accessError
    ) {
        return AnswerCheck.<Mail>builder()
                .check(mail -> personInformation.getTelegramId().equals(mail.getPersonId()))
                .unitTrue(checkFirstStart)
                .unitFalse(accessError)
                .build();
    }

    @Unit(value = ACCESS_ERROR)
    public AnswerText<Mail> accessError() {
        return AnswerText.<Mail>builder()
                .answer(message -> {
                    final String messageText = new StringBuilder("\uD83D\uDEA8 *Попытка несанкционированного доступа к боту*")
                            .append(Smile.HR.getValue())
                            .append("\uD83E\uDDB9\u200D♂️: ").append(message.getPersonId()).append("\n")
                            .append("\uD83D\uDCAC: ").append(message.getText())
                            .toString();
                    return BoxAnswer.builder().recipientPersonId(personInformation.getTelegramId()).message(messageText).build();
                })
                .build();
    }

    @Unit(value = CHECK_FIRST_START)
    public AnswerCheck<Mail> checkFirstStart(
            @Unit(TEXT_PARSER_PRIVATE_PROJECT) MainUnit<Mail> textParserPrivateProject,
            @Unit(CHECK_MENU_OR_ANSWER) MainUnit<Mail> checkMenuOrAnswer
    ) {
        return AnswerCheck.<Mail>builder()
                .check(message -> settingService.isFirstStart())
                .unitFalse(checkMenuOrAnswer)
                .unitTrue(textParserPrivateProject)
                .build();
    }

    @Unit(value = CHECK_MENU_OR_ANSWER)
    public AnswerCheck<Mail> checkMenuOrAnswer(
            @Unit(GENERAL_MENU) MainUnit<Mail> menu,
            @Unit(ANSWER_NOTE) MainUnit<Mail> answerNote
    ) {
        return AnswerCheck.<Mail>builder()
                .check(
                        mail -> {
                            final List<Mail> forwardMails = mail.getForwardMail();
                            if (forwardMails != null && forwardMails.size() == 1) {
                                final Mail forwardMail = forwardMails.get(0);
                                return Attachments.findFirstLink(forwardMail.getAttachments()).isPresent();
                            }
                            return false;
                        }
                )
                .unitTrue(answerNote)
                .unitFalse(menu)
                .build();
    }

    @Unit(ANSWER_NOTE)
    public AnswerText<Mail> answerNote() {
        return AnswerText.<Mail>builder()
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

    @Unit(TEXT_PARSER_PRIVATE_PROJECT)
    public AnswerText<Mail> textParserPrivateProject(
            @Unit(CHECK_PARSER_PRIVATE_PROJECT) MainUnit<Mail> checkParserPrivateProject
    ) {
        return AnswerText.<Mail>builder()
                .answer(() -> boxAnswer(
                                "Start tracking private projects?",
                                inlineKeyBoard(
                                        simpleLine(
                                                simpleButton("Yes", "YES"),
                                                simpleButton("No", "NO")
                                        )
                                )
                        )
                )
                .activeType(UnitActiveType.AFTER)
                .next(checkParserPrivateProject)
                .build();
    }

    @Unit(CHECK_PARSER_PRIVATE_PROJECT)
    public AnswerCheck<Mail> checkParserPrivateProject(
            @Unit(PARSER_PRIVATE_PROJECT) AnswerText<Mail> parserPrivateProject,
            @Unit(TEXT_PARSE_OWNER_PROJECT) MainUnit<Mail> textParseOwnerProject
    ) {
        return AnswerCheck.<Mail>builder()
                .check(mail -> "YES".equalsIgnoreCase(mail.getText()))
                .intermediateAnswerIfTrue(replaceBoxAnswer("Scanning of private projects has begun. Wait..."))
                .unitTrue(parserPrivateProject)
                .unitFalse(textParseOwnerProject)
                .build();
    }

    @Unit(PARSER_PRIVATE_PROJECT)
    public AnswerText<Mail> parserPrivateProject(
            @Unit(TEXT_PARSE_OWNER_PROJECT) MainUnit<Mail> textParseOwnerProject
    ) {
        return AnswerText.<Mail>builder()
                .answer(() -> {
                    notifyService.disableAllNotify();
                    projectParser.parseAllPrivateProject();
                    return replaceBoxAnswer("Projects have been successfully added to tracking");
                })
                .next(textParseOwnerProject)
                .build();
    }

    @Unit(TEXT_PARSE_OWNER_PROJECT)
    public AnswerText<Mail> textParseOwnerProject(
            @Unit(CHECK_PARSE_OWNER_PROJECT) MainUnit<Mail> checkParseOwnerProject
    ) {
        return AnswerText.<Mail>builder()
                .answer(
                        boxAnswer(
                                "Start tracking public projects that you own?",
                                inlineKeyBoard(
                                        simpleLine(
                                                simpleButton("Yes", "YES"),
                                                simpleButton("No", "NO")
                                        )
                                )
                        )
                )
                .activeType(UnitActiveType.AFTER)
                .next(checkParseOwnerProject)
                .build();
    }

    @Unit(CHECK_PARSE_OWNER_PROJECT)
    public AnswerCheck<Mail> checkParseOwnerProject(
            @Unit(PARSE_OWNER_PROJECT) MainUnit<Mail> parseOwnerProject,
            @Unit(END_SETTING) MainUnit<Mail> endSetting
    ) {
        return AnswerCheck.<Mail>builder()
                .check(message -> "YES".equalsIgnoreCase(message.getText()))
                .intermediateAnswerIfTrue(replaceBoxAnswer("Scanning of public projects has begun. Wait..."))
                .unitTrue(parseOwnerProject)
                .unitFalse(endSetting)
                .build();
    }

    @Unit(PARSE_OWNER_PROJECT)
    public AnswerText<Mail> parseOwnerProject(
            @Unit(END_SETTING) MainUnit<Mail> endSetting
    ) {
        return AnswerText.<Mail>builder()
                .answer(() -> {
                    projectParser.parseAllProjectOwner();
                    return replaceBoxAnswer("Projects have been successfully added to tracking");
                })
                .next(endSetting)
                .build();
    }

    @Unit(END_SETTING)
    public AnswerText<Mail> endSetting() {
        return AnswerText.<Mail>builder()
                .answer(
                        () -> {
                            settingService.disableFirstStart();
                            notifyService.enableAllNotify();
                            return replaceBoxAnswer("""
                                    Configuration completed successfully
                                    Developer: [uPagge](https://mark.struchkov.dev)
                                    """);
                        }
                )
                .activeType(UnitActiveType.AFTER)
                .build();
    }

}
