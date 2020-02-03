package com.tsc.bitbucketbot.config.unit;

import org.sadtech.social.bot.domain.unit.AnswerCheck;
import org.sadtech.social.bot.domain.unit.AnswerText;
import org.sadtech.social.bot.domain.unit.MainUnit;
import org.sadtech.social.core.domain.BoxAnswer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [02.02.2020]
 */
@Configuration
public class PanelUnit {

    @Bean
    public AnswerText textEntranceAdmin(
            MainUnit checkPasswordEntranceAdmin
    ) {
        return AnswerText.builder()
                .boxAnswer(BoxAnswer.of("Введите пароль"))
                .phrase("Панель управления")
                .nextUnit(checkPasswordEntranceAdmin)
                .build();
    }

    @Bean
    public AnswerCheck checkPasswordEntranceAdmin(
            @Value("${bitbucketbot.panel.password}") String password
    ){
        return AnswerCheck.builder()
                .check(message -> password.equals(message.getText()))
                .unitFalse(AnswerText.of("Пароль неверный"))
//                .unitTrue()
                .build();
    }

}
