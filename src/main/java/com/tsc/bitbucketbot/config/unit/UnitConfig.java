package com.tsc.bitbucketbot.config.unit;

import com.tsc.bitbucketbot.service.UserService;
import org.sadtech.social.bot.domain.unit.AnswerCheck;
import org.sadtech.social.bot.domain.unit.AnswerText;
import org.sadtech.social.bot.domain.unit.MainUnit;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.utils.KeyBoards;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Configuration
public class UnitConfig {

    @Bean
    public AnswerCheck checkMenu(
            UserService userService,
            @Value("${bitbucketbot.telegram.admin-chatid}") Long adminChatId,
            AnswerText menu
    ) {
        return AnswerCheck.builder()
                .check(message -> !userService.existsByTelegramId(message.getPersonId()) || message.getPersonId().equals(adminChatId))
                .unitTrue(menu)
                .unitFalse(AnswerText.of("Вы уже получаете уведомления"))
                .build();
    }

    @Bean
    public AnswerText menu(
            MainUnit entranceText,
            MainUnit textEntranceAdmin
    ) {
        return AnswerText.builder()
                .boxAnswer(
                        BoxAnswer.builder()
                                .message("Привет. Я помогаю сотрудникам ТСК отслеживать события в Bitbucket. Если хочешь войти, обращайся к @upagge")
                                .keyBoard(KeyBoards.verticalMenuString("Войти", "Панель управления"))
                                .build()
                )
                .nextUnit(entranceText)
                .nextUnit(textEntranceAdmin)
                .build();
    }

}
