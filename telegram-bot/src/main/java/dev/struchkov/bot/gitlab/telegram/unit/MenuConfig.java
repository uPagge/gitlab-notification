package dev.struchkov.bot.gitlab.telegram.unit;

import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.bot.gitlab.context.service.NoteService;
import dev.struchkov.bot.gitlab.context.utils.Smile;
import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.bot.gitlab.core.service.parser.ProjectParser;
import dev.struchkov.bot.gitlab.telegram.utils.Keys;
import dev.struchkov.bot.gitlab.telegram.utils.UnitName;
import dev.struchkov.godfather.main.domain.BoxAnswer;
import dev.struchkov.godfather.main.domain.annotation.Unit;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.core.unit.AnswerText;
import dev.struchkov.godfather.simple.core.unit.MainUnit;
import dev.struchkov.godfather.simple.data.StorylineContext;
import dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import dev.struchkov.haiti.utils.Checker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.ACCESS_ERROR;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.ADD_NEW_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.GENERAL_MENU;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.GET_ASSIGNEE_MERGE_REQUEST;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.GET_TASKS;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.SETTINGS;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_ADD_NEW_PROJECT;
import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.godfather.main.domain.BoxAnswer.replaceBoxAnswer;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine.simpleLine;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;
import static dev.struchkov.godfather.telegram.simple.core.util.TriggerChecks.clickButtonRaw;

/**
 * // TODO: 16.01.2021 Добавить описание.
 *
 * @author upagge 16.01.2021
 */
@Component
@RequiredArgsConstructor
public class MenuConfig {

    private final StorylineContext context;
    private final TelegramSending sending;

    private final ProjectParser projectParser;
    private final GitlabProperty gitlabProperty;
    private final PersonInformation personInformation;
    private final NoteService noteService;
    private final MergeRequestsService mergeRequestsService;
    private final AppSettingService settingService;

    @Unit(value = ACCESS_ERROR, main = true)
    public AnswerText<Mail> accessError() {
        return AnswerText.<Mail>builder()
                .triggerCheck(mail -> !personInformation.getTelegramId().equals(mail.getPersonId()))
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

    @Unit(value = GENERAL_MENU, main = true)
    public AnswerText<Mail> menu(
            @Unit(SETTINGS) MainUnit<Mail> settings,
            @Unit(TEXT_ADD_NEW_PROJECT) MainUnit<Mail> textAddNewProject,
            @Unit(GET_TASKS) MainUnit<Mail> getTasks,
            @Unit(GET_ASSIGNEE_MERGE_REQUEST) MainUnit<Mail> getAssigneeMergeRequest
    ) {
        return AnswerText.<Mail>builder()
                .priority(5)
                .triggerCheck(mail -> {
                    final boolean isAccess = personInformation.getTelegramId().equals(mail.getPersonId());
                    if (isAccess) {
                        final boolean firstStart = settingService.isFirstStart();
                        return !firstStart;
                    }
                    return false;
                })
                .answer(mail -> {
                            final String messageText = "This is the bot menu, select a new item";
                            final InlineKeyBoard generalMenuKeyBoard = inlineKeyBoard(
                                    simpleLine(simpleButton("Add project", TEXT_ADD_NEW_PROJECT)),
                                    simpleLine(
                                            simpleButton("My tasks", GET_TASKS),
                                            simpleButton("Merge Request", GET_ASSIGNEE_MERGE_REQUEST)
                                    ),
                                    simpleLine(simpleButton("Settings", SETTINGS))
                            );
                            final String personId = mail.getPersonId();
                            final var initSettingFinish = context.removeKey(personId, Keys.INIT_SETTING_FINISH);
                            if (initSettingFinish.isPresent()) {
                                context.removeKey(personId, Keys.INIT_SETTING_PRIVATE_PROJECT_MESSAGE_ID).ifPresent(messageId -> sending.deleteMessage(personId, messageId));
                                context.removeKey(personId, Keys.INIT_SETTING_PUBLIC_PROJECT_MESSAGE_ID).ifPresent(messageId -> sending.deleteMessage(personId, messageId));
                                return replaceBoxAnswer(messageText, generalMenuKeyBoard);
                            }
                            return boxAnswer(messageText, generalMenuKeyBoard);
                        }
                )
                .next(settings)
                .next(textAddNewProject)
                .next(getTasks)
                .next(getAssigneeMergeRequest)
                .build();
    }

    @Unit(TEXT_ADD_NEW_PROJECT)
    public AnswerText<Mail> textAddNewProject(
            @Unit(ADD_NEW_PROJECT) MainUnit<Mail> addNewProject
    ) {
        return AnswerText.<Mail>builder()
                .triggerCheck(clickButtonRaw(TEXT_ADD_NEW_PROJECT))
                .answer(boxAnswer("Copy the url of the project and send it to me"))
                .next(addNewProject)
                .build();
    }

    @Unit(ADD_NEW_PROJECT)
    public AnswerText<Mail> addNewProject() {
        return AnswerText.<Mail>builder()
                .answer(mail -> {
                    final String mailText = mail.getText();
                    final String projectUrl = gitlabProperty.getProjectAddUrl() + mailText.replace(gitlabProperty.getBaseUrl(), "")
                            .substring(1)
                            .replace("/", "%2F");
                    projectParser.parseByUrl(projectUrl);
                    return boxAnswer("Project added successfully");
                })
                .build();
    }

    @Unit(SETTINGS)
    public AnswerText<Mail> settings() {
        return AnswerText.<Mail>builder()
                .triggerPhrase(SETTINGS)
                .answer(boxAnswer("This is the settings menu"))
                .build();
    }

    @Unit(GET_TASKS)
    public AnswerText<Mail> getTasks() {
        return AnswerText.<Mail>builder()
                .triggerPhrase(GET_TASKS)
                .answer(
                        () -> {
                            final Long userId = personInformation.getId();
                            final String text = noteService.getAllPersonTask(userId, false).stream()
                                    .map(note -> MessageFormat.format("- [{0}]({1})", trim(note.getBody()).replace("\n", " "), note.getWebUrl()))
                                    .collect(Collectors.joining("\n"));
                            return boxAnswer("".equals(text) ? "No tasks found" : text);
                        }
                )
                .build();
    }

    private String trim(String body) {
        return body.length() > 31 ? body.substring(0, 30) : body;
    }

    @Unit(UnitName.GET_ASSIGNEE_MERGE_REQUEST)
    public AnswerText<Mail> getAssigneeMergeRequest() {
        return AnswerText.<Mail>builder()
                .triggerPhrase(GET_ASSIGNEE_MERGE_REQUEST)
                .answer(() -> {
                    final Long gitlabUserId = personInformation.getId();
                    final List<MergeRequest> mergeRequests = mergeRequestsService.getAllByReviewerId(gitlabUserId);
                    if (Checker.checkNotEmpty(mergeRequests)) {
                        final String text = mergeRequests.stream()
                                .map(mergeRequest -> MessageFormat.format("[{0}]({1})", mergeRequest.getTitle(), mergeRequest.getWebUrl()))
                                .collect(Collectors.joining("\n"));
                        return boxAnswer(text);
                    }
                    return boxAnswer("You are not assigned in charge of MR");
                })
                .build();
    }

}
