package com.tsc.bitbucketbot.config.unit;

import com.tsc.bitbucketbot.service.UserService;
import org.sadtech.social.bot.domain.unit.AnswerCheck;
import org.sadtech.social.bot.domain.unit.AnswerText;
import org.sadtech.social.bot.domain.unit.MainUnit;
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
public class UnitConfig {

    @Bean
    public AnswerCheck checkMenu(
            UserService userService,
            AnswerText menu,
            AnswerText generalMenu
    ) {
        return AnswerCheck.builder()
                .check(message -> !userService.existsByTelegramId(message.getPersonId()))
                .unitTrue(menu)
                .unitFalse(generalMenu)
                .build();
    }

    @Bean
    public AnswerText menu(
            MainUnit entranceText
    ) {
        return AnswerText.builder()
                .boxAnswer(
                        BoxAnswer.builder()
                                .message("Привет. Я помогаю сотрудникам ТСК отслеживать события в Bitbucket.")
                                .keyBoard(KeyBoards.verticalMenuString("Войти"))
                                .build()
                )
                .nextUnit(entranceText)
                .build();
    }

    @Bean
    public AnswerText generalMenu() {
        return AnswerText.builder()
                .boxAnswer(
                        BoxAnswer.builder()
                                .message("Привет. Ты уже авторизован. Возможно тут появятся новые фичи... Но это не точно\nПо вопросам функциональности бота пиши сюда: @upagge")
                                .build()
                )
                .build();
    }

}
