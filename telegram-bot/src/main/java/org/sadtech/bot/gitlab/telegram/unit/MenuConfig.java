package org.sadtech.bot.gitlab.telegram.unit;

import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.PersonInformation;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.filter.MergeRequestFilter;
import org.sadtech.bot.gitlab.context.service.AppSettingService;
import org.sadtech.bot.gitlab.context.service.DiscussionService;
import org.sadtech.bot.gitlab.context.service.MergeRequestsService;
import org.sadtech.bot.gitlab.core.config.properties.GitlabProperty;
import org.sadtech.bot.gitlab.core.service.parser.ProjectParser;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.core.page.PaginationImpl;
import org.sadtech.social.bot.domain.unit.AnswerText;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.domain.keyboard.KeyBoard;
import org.sadtech.social.core.domain.keyboard.KeyBoardLine;
import org.sadtech.social.core.domain.keyboard.button.KeyBoardButtonText;
import org.sadtech.social.core.utils.KeyBoards;
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
    public AnswerText menu(
            AppSettingService settingService,
            AnswerText settings,
            AnswerText textAddNewProject,
            AnswerText getTasks,
            AnswerText getAssigneeMergeRequest
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
    public AnswerText textAddNewProject(
            AppSettingService settingService,
            AnswerText addNewProject
    ) {
        return AnswerText.builder()
                .boxAnswer(BoxAnswer.processing(settingService.getMessage("ui.menu.add_mr.text")))
                .phrase(settingService.getMessage("ui.menu.add_mr"))
                .nextUnit(addNewProject)
                .build();
    }

    @Bean
    public AnswerText addNewProject(
            AppSettingService settingService,
            ProjectParser projectParser,
            GitlabProperty gitlabProperty
    ) {
        return AnswerText.builder()
                .boxAnswer(message -> {
                    final List<String> urlList = Arrays.stream(message.getText().split("/")).collect(Collectors.toList());
                    int lastElement = urlList.size() - 1;
                    final String projectUrl = MessageFormat.format(gitlabProperty.getUrlMergeRequestAdd(), urlList.get(lastElement - 1), urlList.get(lastElement));
                    projectParser.parseByUrl(projectUrl);
                    return BoxAnswer.of(settingService.getMessage("menu.add_project_success"));
                })
                .build();
    }

    @Bean
    public AnswerText settings(
            AppSettingService settingService,
            AnswerText settingsLanguage
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
    public AnswerText getTasks(
            DiscussionService discussionService,
            AppSettingService settingService,
            PersonInformation personInformation
    ) {
        return AnswerText.builder()
                .boxAnswer(message ->
                {
                    final Long userId = personInformation.getId();
                    final String text = "test";
//                            taskService.getAllPersonTask(userId, false).stream()
//                            .collect(Collectors.groupingBy(Task::getMergeRequest))
//                            .entrySet()
//                            .stream()
//                            .map(node -> {
//                                final String mrTitle = node.getKey().getTitle();
//                                final String mrUrl = node.getKey().getWebUrl();
//
//                                final String taskText = node.getValue().stream()
//                                        .map(task -> MessageFormat.format("[{0}]({1})", task.getBody(), task.getWebUrl()))
//                                        .collect(Collectors.joining("\n"));
//
//                                return MessageFormat.format("- [{0}]({1}):\n{2}", mrTitle, mrUrl, taskText);
//                            })
//                            .collect(Collectors.joining("\n\n"));
                    return BoxAnswer.of("".equals(text) ? settingService.getMessage("ui.answer.no_task") : text);
                })
                .phrase(settingService.getMessage("ui.menu.task"))
                .build();
    }

    @Bean
    public AnswerText getAssigneeMergeRequest(
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
