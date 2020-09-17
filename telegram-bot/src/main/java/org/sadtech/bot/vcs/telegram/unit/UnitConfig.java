package org.sadtech.bot.vcs.telegram.unit;

import org.sadtech.social.bot.domain.unit.AnswerProcessing;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.domain.content.Mail;
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
    public AnswerProcessing<Mail> menu() {
        return AnswerProcessing.<Mail>builder()
                .processingData(message ->
                        BoxAnswer.builder()
                                .message("Привет :)\nЭтот бот сообщает о появлении новых ПР и об изменениях в старых\n\n" +
                                        "Теперь когда ты знаешь правду, ты просто обязан отправь POST запрос на адрес " +
                                        "http://192.168.236.164:8018/api/user/reg\n\n" +
                                        "В теле запроса укажи следующее:\n\n" +
                                        "{\n" +
                                        "\t\"telegramId\": " + message.getPersonId() + ",\n" +
                                        "\t\"login\": \"apetrov\",\n" +
                                        "\t\"token\": \"token value\"\n" +
                                        "}" +
                                        "\n\n" +
                                        "ВНИМАНИЕ!!!\ntelegramId не менять; login как в bitbucket; токен получать [тут](http://192.168.236.164:7990/plugins/servlet/access-tokens/manage)" +
                                        "\n-- -- --\n" +
                                        "По всем вопросам обращаться к @uPagge")
                                .build()
                )
                .build();
    }

}
