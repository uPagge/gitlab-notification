package dev.struchkov.bot.gitlab.telegram.unit;

import dev.struchkov.bot.gitlab.context.domain.AppLocale;
import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.service.DiscussionService;
import dev.struchkov.bot.gitlab.context.service.NoteService;
import dev.struchkov.bot.gitlab.core.service.parser.ProjectParser;
import dev.struchkov.godfather.context.domain.BoxAnswer;
import dev.struchkov.godfather.context.domain.content.Mail;
import dev.struchkov.godfather.context.domain.content.Message;
import dev.struchkov.godfather.context.domain.content.attachment.Attachment;
import dev.struchkov.godfather.context.domain.content.attachment.AttachmentType;
import dev.struchkov.godfather.context.domain.content.attachment.Link;
import dev.struchkov.godfather.context.utils.KeyBoards;
import dev.struchkov.godfather.core.domain.unit.AnswerCheck;
import dev.struchkov.godfather.core.domain.unit.AnswerProcessing;
import dev.struchkov.godfather.core.domain.unit.AnswerText;
import dev.struchkov.godfather.core.domain.unit.UnitActiveType;
import dev.struchkov.haiti.context.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.MessageFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Configuration
@RequiredArgsConstructor
public class UnitConfig {

    private static final Pattern NOTE_LINK = Pattern.compile("#note_\\d+$");

    @Bean
    public AnswerCheck checkFirstStart(
            AppSettingService settingService,
            AnswerText<Message> textCheckLanguage,
            AnswerCheck checkMenuOrAnswer
    ) {
        return AnswerCheck.builder()
                .check(
                        message -> settingService.isFirstStart()
                )
                .unitFalse(checkMenuOrAnswer)
                .unitTrue(textCheckLanguage)
                .build();
    }

    @Bean
    public AnswerCheck checkMenuOrAnswer(
            AnswerText<Message> menu,
            AnswerText<Message> answerNote
    ) {
        return AnswerCheck.builder()
                .check(
                        message -> {
                            Mail mail = (Mail) message;
                            final List<Mail> forwardMails = mail.getForwardMail();
                            if (forwardMails != null && forwardMails.size() == 1) {
                                final Mail forwardMail = forwardMails.get(0);
                                return forwardMail.getAttachments().stream()
                                        .anyMatch(attachment -> AttachmentType.LINK.equals(attachment.getType()));
                            }
                            return false;
                        }
                )
                .unitTrue(answerNote)
                .unitFalse(menu)
                .build();
    }

    @Bean
    public AnswerText<Message> answerNote(
            NoteService noteService,
            DiscussionService discussionService
    ) {
        return AnswerText.builder()
                .boxAnswer(
                        message -> {
                            final List<Attachment> attachments = ((Mail) message).getForwardMail().get(0).getAttachments();
                            for (Attachment attachment : attachments) {
                                if (AttachmentType.LINK.equals(attachment.getType())) {
                                    final String url = ((Link) attachment).getUrl();
                                    Matcher matcher = NOTE_LINK.matcher(url);
                                    if (matcher.find()) {
                                        final String noteText = url.substring(matcher.start(), matcher.end());
                                        Long noteId = Long.valueOf(noteText.replaceAll("#note_", ""));
                                        final Note note = noteService.getById(noteId).orElseThrow(NotFoundException.supplier("Note не найдено"));
                                        final String discussionId = note.getDiscussion().getId();
                                        discussionService.answer(discussionId, MessageFormat.format("@{0}, {1}", note.getAuthor().getUserName(), message.getText()));
                                    }
                                    return BoxAnswer.of("");
                                }
                            }
                            return BoxAnswer.of("Ошибка");
                        }
                )
                .build();
    }

    @Bean
    public AnswerText<Message> textCheckLanguage(
            AnswerProcessing checkLanguage
    ) {
        return AnswerText.builder()
                .boxAnswer(message ->
                        BoxAnswer.builder()
                                .message("Hi :)\n\nLet's choose a language for.")
                                .keyBoard(KeyBoards.verticalDuoMenuString("Русский", "English"))
                                .build()
                )
                .nextUnit(checkLanguage)
                .build();
    }

    @Bean
    public AnswerProcessing checkLanguage(
            AppSettingService settingService,
            AnswerText<Message> textParserPrivateProject
    ) {
        return AnswerProcessing
                .builder()
                .processingData(
                        message -> {
                            final AppLocale appLocale = AppLocale.of(message.getText());
                            settingService.setLocale(appLocale);
                            return BoxAnswer.of(
                                    settingService.getMessage("ui.lang_changed")
                            );
                        }
                )
                .nextUnit(textParserPrivateProject)
                .build();
    }

    @Bean
    public AnswerText<Message> textParserPrivateProject(
            AnswerCheck checkParserPrivateProject,
            AppSettingService settingService
    ) {
        return AnswerText.builder()
                .boxAnswer(message ->
                        BoxAnswer.builder()
                                .message(settingService.getMessage("ui.monitor_private_projects"))
                                .keyBoard(KeyBoards.verticalDuoMenuString(
                                        settingService.getMessage("main.yes"), settingService.getMessage("main.no")
                                ))
                                .build()
                )
                .activeType(UnitActiveType.AFTER)
                .nextUnit(checkParserPrivateProject)
                .build();
    }

    @Bean
    public AnswerCheck checkParserPrivateProject(
            AppSettingService appSettingService,
            AnswerProcessing parserPrivateProject,
            AnswerText<Message> textParseOwnerProject
    ) {
        return AnswerCheck.builder()
                .check(
                        message -> appSettingService.getMessage("main.yes").equalsIgnoreCase(message.getText())
                )
                .unitTrue(parserPrivateProject)
                .unitFalse(textParseOwnerProject)
                .build();
    }

    @Bean
    public AnswerProcessing parserPrivateProject(
            ProjectParser projectParser,
            AppSettingService settingService,
            AnswerText<Message> textParseOwnerProject
    ) {
        return AnswerProcessing.builder()
                .processingData(message -> {
                    projectParser.parseAllPrivateProject();
                    return BoxAnswer.of(settingService.getMessage("ui.monitor_project_private_success"));
                })
                .nextUnit(textParseOwnerProject)
                .build();
    }

    @Bean
    public AnswerText<Message> textParseOwnerProject(
            AppSettingService settingService,
            AnswerCheck checkParseOwnerProject
    ) {
        return AnswerText.builder()
                .boxAnswer(message ->
                        BoxAnswer.builder()
                                .message(settingService.getMessage("ui.monitor_owner_projects"))
                                .keyBoard(KeyBoards.verticalDuoMenuString(
                                        settingService.getMessage("main.yes"), settingService.getMessage("main.no")
                                ))
                                .build()
                )
                .activeType(UnitActiveType.AFTER)
                .nextUnit(checkParseOwnerProject)
                .build();
    }

    @Bean
    public AnswerCheck checkParseOwnerProject(
            AppSettingService appSettingService,
            AnswerProcessing parseOwnerProject,
            AnswerProcessing endSetting
    ) {
        return AnswerCheck.builder()
                .check(
                        message -> appSettingService.getMessage("main.yes").equalsIgnoreCase(message.getText())
                )
                .unitTrue(parseOwnerProject)
                .unitFalse(endSetting)
                .build();
    }

    @Bean
    public AnswerProcessing parseOwnerProject(
            ProjectParser projectParser,
            AppSettingService settingService,
            AnswerProcessing endSetting
    ) {
        return AnswerProcessing.builder()
                .processingData(message -> {
                    projectParser.parseAllProjectOwner();
                    return BoxAnswer.of(settingService.getMessage("ui.monitor_project_private_success"));
                })
                .nextUnit(endSetting)
                .build();
    }

    @Bean
    public AnswerProcessing endSetting(
            AppSettingService settingService
    ) {
        return AnswerProcessing.builder()
                .processingData(
                        message -> {
                            settingService.disableFirstStart();
                            return BoxAnswer.of(settingService.getMessage("ui.setup_finished"));
                        }
                )
                .activeType(UnitActiveType.AFTER)
                .build();
    }

}
