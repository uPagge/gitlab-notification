package org.sadtech.bot.gitlab.telegram.unit;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.AppLocale;
import org.sadtech.bot.gitlab.context.service.AppSettingService;
import org.sadtech.bot.gitlab.core.service.parser.ProjectParser;
import org.sadtech.social.bot.domain.unit.AnswerCheck;
import org.sadtech.social.bot.domain.unit.AnswerProcessing;
import org.sadtech.social.bot.domain.unit.AnswerText;
import org.sadtech.social.bot.domain.unit.UnitActiveType;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.utils.KeyBoards;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Configuration
@RequiredArgsConstructor
public class UnitConfig {

    @Bean
    public AnswerCheck checkFirstStart(
            AppSettingService settingService,
            AnswerText textCheckLanguage
    ) {
        return AnswerCheck.builder()
                .check(
                        message -> settingService.isFirstStart()
                )
                .unitTrue(textCheckLanguage)
                .build();
    }

    @Bean
    public AnswerText textCheckLanguage(
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
            AnswerText textParserPrivateProject
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
    public AnswerText textParserPrivateProject(
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
            AnswerText textParseOwnerProject
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
            AnswerText textParseOwnerProject
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
    public AnswerText textParseOwnerProject(
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
