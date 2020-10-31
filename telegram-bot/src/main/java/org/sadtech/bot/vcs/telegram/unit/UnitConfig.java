package org.sadtech.bot.vcs.telegram.unit;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.vcs.telegram.service.unit.RatingTopProcessing;
import org.sadtech.bot.vcs.telegram.service.unit.TaskProcessing;
import org.sadtech.bot.vcs.telegram.utils.GeneratorKeyBoards;
import org.sadtech.bot.vsc.bitbucketbot.context.service.PersonService;
import org.sadtech.social.bot.domain.unit.AnswerCheck;
import org.sadtech.social.bot.domain.unit.AnswerProcessing;
import org.sadtech.social.bot.domain.unit.AnswerText;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.domain.content.Mail;
import org.sadtech.social.core.domain.content.Message;
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

    private final PersonService personService;

    @Bean
    public AnswerCheck regCheck(
            AnswerProcessing<Mail> noRegister,
            AnswerText menu
    ) {
        return AnswerCheck.builder()
                .check(
                        message -> personService.existsByTelegram(message.getPersonId())
                )
                .unitFalse(noRegister)
                .unitTrue(menu)
                .build();
    }

    @Bean
    public AnswerText menu(
            AnswerProcessing<Message> getTasks,
            AnswerText menuPullRequest,
            AnswerText settings,
            AnswerProcessing<Message> getTopRating
    ) {
        return AnswerText.builder()
                .boxAnswer(
                        BoxAnswer.builder()
                                .message("Привет, выбери пункт меню!")
                                .keyBoard(GeneratorKeyBoards.menu())
                                .build()
                )
                .nextUnit(getTasks)
                .nextUnit(menuPullRequest)
                .nextUnit(settings)
                .nextUnit(getTopRating)
                .build();
    }

    @Bean
    public AnswerText settings(
            AnswerText notifySetting
    ) {
        return AnswerText.builder()
                .boxAnswer(
                        BoxAnswer.builder()
                                .message("Здесь вы можете персонализировать бота")
                                .keyBoard(
                                        KeyBoards.verticalMenuString("Уведомления")
                                )
                                .build()
                )
                .phrase("Настройки")
                .nextUnit(notifySetting)
                .build();
    }

    @Bean
    public AnswerProcessing<Message> getTasks(
            TaskProcessing taskProcessing
    ) {
        return AnswerProcessing.builder()
                .processingData(taskProcessing)
                .phrase("Мои задачи")
                .build();
    }

    @Bean
    AnswerProcessing<Message> getTopRating(
            RatingTopProcessing ratingTopProcessing
    ) {
        return AnswerProcessing.builder()
                .processingData(ratingTopProcessing)
                .keyWord("таблица")
                .keyWord("рейтинга")
                .build();
    }

    @Bean
    public AnswerProcessing<Mail> noRegister() {
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
