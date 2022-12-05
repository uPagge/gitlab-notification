package dev.struchkov.bot.gitlab.telegram.unit;

import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.filter.MergeRequestFilter;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.bot.gitlab.context.service.NoteService;
import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.bot.gitlab.core.service.parser.ProjectParser;
import dev.struchkov.bot.gitlab.telegram.utils.UnitName;
import dev.struchkov.godfather.main.domain.annotation.Unit;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.core.unit.AnswerText;
import dev.struchkov.godfather.simple.core.unit.MainUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.ADD_NEW_PROJECT;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.GENERAL_MENU;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.GET_ASSIGNEE_MERGE_REQUEST;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.GET_TASKS;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.SETTINGS;
import static dev.struchkov.bot.gitlab.telegram.utils.UnitName.TEXT_ADD_NEW_PROJECT;
import static dev.struchkov.godfather.main.domain.BoxAnswer.boxAnswer;
import static dev.struchkov.godfather.main.domain.keyboard.button.SimpleButton.simpleButton;
import static dev.struchkov.godfather.main.domain.keyboard.simple.SimpleKeyBoardLine.simpleLine;
import static dev.struchkov.godfather.telegram.domain.keyboard.InlineKeyBoard.inlineKeyBoard;

/**
 * // TODO: 16.01.2021 Добавить описание.
 *
 * @author upagge 16.01.2021
 */
@Component
@RequiredArgsConstructor
public class MenuConfig {

    private final ProjectParser projectParser;
    private final GitlabProperty gitlabProperty;
    private final PersonInformation personInformation;
    private final NoteService noteService;
    private final MergeRequestsService mergeRequestsService;

    @Unit(GENERAL_MENU)
    public AnswerText<Mail> menu(
            @Unit(SETTINGS) MainUnit<Mail> settings,
            @Unit(TEXT_ADD_NEW_PROJECT) MainUnit<Mail> textAddNewProject,
            @Unit(GET_TASKS) MainUnit<Mail> getTasks,
            @Unit(GET_ASSIGNEE_MERGE_REQUEST) MainUnit<Mail> getAssigneeMergeRequest
    ) {
        return AnswerText.<Mail>builder()
                .answer(boxAnswer(
                                "This is the bot menu, select a new item",
                                inlineKeyBoard(
                                        simpleLine(simpleButton("Add project", TEXT_ADD_NEW_PROJECT)),
                                        simpleLine(
                                                simpleButton("My tasks", GET_TASKS),
                                                simpleButton("Merge Request", GET_ASSIGNEE_MERGE_REQUEST)
                                        ),
                                        simpleLine(simpleButton("Settings", SETTINGS))
                                )
                        )
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
                .triggerPhrase(TEXT_ADD_NEW_PROJECT)
                .answer(boxAnswer("Copy the url of the project and send it to me"))
                .next(addNewProject)
                .build();
    }

    @Unit(ADD_NEW_PROJECT)
    public AnswerText<Mail> addNewProject() {
        return AnswerText.<Mail>builder()
                .answer(mail -> {
                    final String mailText = mail.getText();
                    final String projectUrl = gitlabProperty.getUrlMergeRequestAdd() + mailText.replace(gitlabProperty.getBaseUrl(), "")
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
                .answer(
                        boxAnswer("This is the settings menu")
                )
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
                    final Long userId = personInformation.getId();
                    final Page<MergeRequest> sheet = mergeRequestsService.getAll(getAssigneeFilter(userId), PageRequest.of(0, 20));
                    if (sheet.hasContent()) {
                        final List<MergeRequest> mergeRequests = sheet.getContent();
                        final String text = mergeRequests.stream()
                                .map(mergeRequest -> MessageFormat.format("[{0}]({1})", mergeRequest.getTitle(), mergeRequest.getWebUrl()))
                                .collect(Collectors.joining("\n"));
                        return boxAnswer(text);
                    }
                    return boxAnswer("You are not assigned in charge of MR");
                })
                .build();
    }

    private MergeRequestFilter getAssigneeFilter(Long userId) {
        final MergeRequestFilter mergeRequestFilter = new MergeRequestFilter();
        mergeRequestFilter.setAssignee(userId);
        mergeRequestFilter.setStates(Collections.singleton(MergeRequestState.OPENED));
        return mergeRequestFilter;
    }

}
