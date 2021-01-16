package org.sadtech.bot.gitlab.telegram.unit.menu;

import org.sadtech.bot.gitlab.context.domain.AppLocale;
import org.sadtech.bot.gitlab.context.service.AppSettingService;
import org.sadtech.social.bot.domain.unit.AnswerText;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.utils.KeyBoards;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * // TODO: 16.01.2021 Добавить описание.
 *
 * @author upagge 16.01.2021
 */
@Configuration
public class MenuSettingsConfig {

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
