package org.sadtech.bot.gitlab.telegram.unit;

import org.sadtech.bot.gitlab.context.service.AppSettingService;
import org.sadtech.bot.gitlab.core.config.properties.GitlabProperty;
import org.sadtech.bot.gitlab.core.service.parser.ProjectParser;
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
            AnswerText textAddNewProject
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

}
