package dev.struchkov.bot.gitlab.telegram.unit;

import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.PersonInformation;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.filter.MergeRequestFilter;
import dev.struchkov.bot.gitlab.context.service.AppSettingService;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.bot.gitlab.context.service.NoteService;
import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.bot.gitlab.core.service.parser.ProjectParser;
import dev.struchkov.godfather.context.domain.BoxAnswer;
import dev.struchkov.godfather.context.domain.content.Message;
import dev.struchkov.godfather.context.domain.keyboard.KeyBoard;
import dev.struchkov.godfather.context.domain.keyboard.KeyBoardLine;
import dev.struchkov.godfather.context.domain.keyboard.button.KeyBoardButtonText;
import dev.struchkov.godfather.context.utils.KeyBoards;
import dev.struchkov.godfather.core.domain.unit.AnswerText;
import dev.struchkov.haiti.context.page.Sheet;
import dev.struchkov.haiti.context.page.impl.PaginationImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * // TODO: 16.01.2021 Добавить описание.
 *
 * @author upagge 16.01.2021
 */
@Configuration
public class MenuConfig {

    @Bean
    public AnswerText<Message> menu(
            AppSettingService settingService,
            AnswerText<Message> settings,
            AnswerText<Message> textAddNewProject,
            AnswerText<Message> getTasks,
            AnswerText<Message> getAssigneeMergeRequest
    ) {
        return AnswerText.builder()
                .boxAnswer(message ->
                        {
                            final KeyBoardButtonText newMr = KeyBoardButtonText.builder().label(settingService.getMessage("ui.menu.add_mr")).build();
                            final KeyBoardButtonText tasks = KeyBoardButtonText.builder().label(settingService.getMessage("ui.menu.task")).build();
                            final KeyBoardButtonText pr = KeyBoardButtonText.builder().label(settingService.getMessage("ui.menu.mr")).build();
                            final KeyBoardButtonText settingsKeyBoard = KeyBoardButtonText.builder().label(settingService.getMessage("ui.menu.setting")).build();

                            final KeyBoardLine oneLine = KeyBoardLine.builder()
                                    .buttonKeyBoard(newMr)
                                    .build();

                            final KeyBoardLine twoLine = KeyBoardLine.builder()
                                    .buttonKeyBoard(tasks)
                                    .buttonKeyBoard(pr)
                                    .build();

                            final KeyBoardLine threeLine = KeyBoardLine.builder()
                                    .buttonKeyBoard(settingsKeyBoard)
                                    .build();

                            final KeyBoard keyBoard = KeyBoard.builder()
                                    .lineKeyBoard(oneLine)
                                    .lineKeyBoard(twoLine)
                                    .lineKeyBoard(threeLine)
                                    .build();

                            return BoxAnswer.builder()
                                    .message(settingService.getMessage("ui.menu.header"))
                                    .keyBoard(keyBoard)
                                    .build();
                        }
                )
                .nextUnit(settings)
                .nextUnit(textAddNewProject)
                .nextUnit(getTasks)
                .nextUnit(getAssigneeMergeRequest)
                .build();
    }

    @Bean
    public AnswerText<Message> textAddNewProject(
            AppSettingService settingService,
            AnswerText<Message> addNewProject
    ) {
        return AnswerText.builder()
                .boxAnswer(BoxAnswer.processing(settingService.getMessage("ui.menu.add_mr.text")))
                .phrase(settingService.getMessage("ui.menu.add_mr"))
                .nextUnit(addNewProject)
                .build();
    }

    @Bean
    public AnswerText<Message> addNewProject(
            AppSettingService settingService,
            ProjectParser projectParser,
            GitlabProperty gitlabProperty
    ) {
        return AnswerText.builder()
                .boxAnswer(message -> {
                    final List<String> urlList = Arrays.stream(message.getText().split("/")).toList();
                    int lastElement = urlList.size() - 1;
                    final String projectUrl = MessageFormat.format(gitlabProperty.getUrlMergeRequestAdd(), urlList.get(lastElement - 1), urlList.get(lastElement));
                    projectParser.parseByUrl(projectUrl);
                    return BoxAnswer.of(settingService.getMessage("menu.add_project_success"));
                })
                .build();
    }

    @Bean
    public AnswerText<Message> settings(
            AppSettingService settingService,
            AnswerText<Message> settingsLanguage
    ) {
        return AnswerText.builder()
                .boxAnswer(message ->
                        BoxAnswer.builder()
                                .message(settingService.getMessage("ui.menu.setting.text"))
                                .keyBoard(KeyBoards.verticalMenuString(settingService.getMessage("ui.menu.setting.language")))
                                .build())
                .phrase(settingService.getMessage("ui.menu.setting"))
                .nextUnit(settingsLanguage)
                .build();
    }

    @Bean
    public AnswerText<Message> getTasks(
            AppSettingService settingService,
            PersonInformation personInformation,
            NoteService noteService
    ) {
        return AnswerText.builder()
                .boxAnswer(message ->
                {
                    final Long userId = personInformation.getId();
                    final String text = noteService.getAllPersonTask(userId, false).stream()
                            .map(note -> MessageFormat.format("- [{0}]({1})", trim(note.getBody()).replace("\n", " "), note.getWebUrl()))
                            .collect(Collectors.joining("\n"));
                    return BoxAnswer.of("".equals(text) ? settingService.getMessage("ui.answer.no_task") : text);
                })
                .phrase(settingService.getMessage("ui.menu.task"))
                .build();
    }

    private String trim(String body) {
        return body.length() > 31 ? body.substring(0, 30) : body;
    }

    @Bean
    public AnswerText<Message> getAssigneeMergeRequest(
            MergeRequestsService mergeRequestsService,
            PersonInformation personInformation,
            AppSettingService settingService
    ) {
        return AnswerText.builder()
                .boxAnswer(message -> {
                    final Long userId = personInformation.getId();
                    final Sheet<MergeRequest> sheet = mergeRequestsService.getAll(getAssigneeFilter(userId), PaginationImpl.of(0, 20));
                    if (sheet.hasContent()) {
                        final List<MergeRequest> mergeRequests = sheet.getContent();
                        final String text = mergeRequests.stream()
                                .map(mergeRequest -> MessageFormat.format("[{0}]({1})", mergeRequest.getTitle(), mergeRequest.getWebUrl()))
                                .collect(Collectors.joining("\n"));
                        return BoxAnswer.of(text);
                    }
                    return BoxAnswer.of(settingService.getMessage("ui.answer.no_mr"));
                })
                .phrase(settingService.getMessage("ui.menu.mr"))
                .build();
    }

    private MergeRequestFilter getAssigneeFilter(Long userId) {
        final MergeRequestFilter mergeRequestFilter = new MergeRequestFilter();
        mergeRequestFilter.setAssignee(userId);
        mergeRequestFilter.setStates(Collections.singleton(MergeRequestState.OPENED));
        return mergeRequestFilter;
    }

}
