package org.sadtech.bot.gitlab.telegram.unit;

import org.sadtech.bot.gitlab.context.domain.AppLocale;
import org.sadtech.bot.gitlab.context.service.AppSettingService;
import org.sadtech.social.bot.domain.unit.AnswerText;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.domain.keyboard.KeyBoard;
import org.sadtech.social.core.domain.keyboard.KeyBoardLine;
import org.sadtech.social.core.domain.keyboard.button.KeyBoardButtonText;
import org.sadtech.social.core.utils.KeyBoards;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
            AnswerText settings
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
    public AnswerText settingsLanguage(
            AppSettingService settingService,
            AnswerText setLanguage
    ) {
        return AnswerText.builder()
                .boxAnswer(message ->
                        BoxAnswer.builder()
                                .message(settingService.getMessage("ui.menu.setting.language.text"))
                                .keyBoard(KeyBoards.verticalDuoMenuString("Русский", "English"))
                                .build())
                .nextUnit(setLanguage)
                .phrase(settingService.getMessage("ui.menu.setting.language"))
                .build();
    }

    @Bean
    public AnswerText setLanguage(
            AppSettingService settingService
    ) {
        return AnswerText.builder()
                .boxAnswer(
                        message -> {
                            final AppLocale appLocale = AppLocale.of(message.getText());
                            settingService.setLocale(appLocale);
                            return BoxAnswer.of(
                                    settingService.getMessage("ui.lang_changed")
                            );
                        }
                )
                .build();
    }

}
