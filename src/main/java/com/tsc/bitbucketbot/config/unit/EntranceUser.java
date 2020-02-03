package com.tsc.bitbucketbot.config.unit;

import com.tsc.bitbucketbot.domain.AuthType;
import com.tsc.bitbucketbot.domain.entity.User;
import com.tsc.bitbucketbot.service.UserService;
import lombok.NonNull;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.sadtech.social.bot.domain.unit.AnswerProcessing;
import org.sadtech.social.bot.domain.unit.AnswerSave;
import org.sadtech.social.bot.domain.unit.AnswerText;
import org.sadtech.social.bot.service.save.LocalPreservable;
import org.sadtech.social.bot.service.save.Preservable;
import org.sadtech.social.core.domain.BoxAnswer;
import org.sadtech.social.core.domain.content.Message;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [02.02.2020]
 */
@Configuration
public class EntranceUser {

    @Bean
    public Preservable<String> authBitbucketPreservable() {
        return new LocalPreservable<>();
    }

    @Bean
    public AnswerText entranceText(
            AnswerSave<String> saveLogin
    ) {
        return AnswerText.builder()
                .boxAnswer(BoxAnswer.of("Пришлите ваш логин в bitbucket"))
                .nextUnit(saveLogin)
                .phrase("Войти")
                .build();
    }

    @Bean
    public AnswerSave<String> saveLogin(
            AnswerText savePasswordText,
            Preservable<String> authBitbucketPreservable
    ) {
        return AnswerSave.<String>builder()
                .preservable(authBitbucketPreservable)
                .key(AuthType.LOGIN.name())
                .preservableData(Message::getText)
                .nextUnit(savePasswordText)
                .build();
    }

    @Bean
    public AnswerText savePasswordText(
            AnswerSave<String> savePassword
    ) {
        return AnswerText.builder()
                .boxAnswer(BoxAnswer.of("Пришлите ваш пароль в bitbucket"))
                .nextUnit(savePassword)
                .build();
    }

    @Bean
    public AnswerSave<String> savePassword(
            Preservable<String> authBitbucketPreservable,
            AnswerText saveTokenText
    ) {
        return AnswerSave.<String>builder()
                .preservable(authBitbucketPreservable)
                .key(AuthType.PASSWORD.name())
                .preservableData(Message::getText)
                .nextUnit(saveTokenText)
                .build();
    }

    @Bean
    public AnswerText saveTokenText(
            AnswerSave<String> saveToken
    ) {
        return AnswerText.builder()
                .boxAnswer(BoxAnswer.of("Пришлите ваш токен в bitbucket.\nПолучить можно здесь: http://192.168.236.164:7990/plugins/servlet/access-tokens/manage"))
                .nextUnit(saveToken)
                .build();
    }

    @Bean
    public AnswerSave<String> saveToken(
            Preservable<String> authBitbucketPreservable,
            AnswerSave<String> saveTelegramId
    ) {
        return AnswerSave.<String>builder()
                .preservable(authBitbucketPreservable)
                .key(AuthType.TOKEN.name())
                .preservableData(Message::getText)
                .nextUnit(saveTelegramId)
                .build();
    }

    @Bean
    public AnswerSave<String> saveTelegramId(
            Preservable<String> authBitbucketPreservable,
            AnswerProcessing<Message> auth
    ) {
        return AnswerSave.<String>builder()
                .preservable(authBitbucketPreservable)
                .key(AuthType.TELEGRAM_ID.name())
                .preservableData(Message::getText)
                .hidden(true)
                .nextUnit(auth)
                .build();
    }

    @Bean
    public AnswerProcessing<Message> auth(
            Preservable<String> authBitbucketPreservable,
            UserService userService
    ) {
        return AnswerProcessing.builder()
                .processingData(message -> {
                    final Optional<String> optLogin = authBitbucketPreservable.getByKey(message.getPersonId(), AuthType.LOGIN.name());
                    final Optional<String> optPassword = authBitbucketPreservable.getByKey(message.getPersonId(), AuthType.PASSWORD.name());
                    final Optional<String> optToken = authBitbucketPreservable.getByKey(message.getPersonId(), AuthType.TOKEN.name());
                    if (optLogin.isPresent() && optPassword.isPresent() && optToken.isPresent()) {
                        try {
                            HttpClient httpClient = HttpClientBuilder.create().build();
                            String encoding = Base64.getEncoder().encodeToString((optLogin.get() + ":" + optPassword.get()).getBytes());
                            HttpGet httpPost = new HttpGet("http://192.168.236.164:7990/");
                            httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
                            HttpResponse response = httpClient.execute(httpPost);
                            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                                return registerNewUser(userService, optLogin.get(), message.getPersonId(), optToken.get());
                            }
                        } catch (IOException e) {
                            BoxAnswer.of("Не удалось авторизоваться");
                        }
                    }
                    return BoxAnswer.of("Не удалось авторизоваться");
                })
                .build();
    }

    private BoxAnswer registerNewUser(@NonNull UserService userService, @NonNull String login, @NonNull Long telegramId, @NonNull String token) {
        final Optional<User> optUser = userService.getByLogin(login);
        if (optUser.isPresent()) {
            final User user = optUser.get();
            if (user.getTelegramId() == null) {
                user.setTelegramId(telegramId);
                user.setToken(token);
                userService.update(user);
                return BoxAnswer.of("Регистрация прошла успешно");
            } else {
                return BoxAnswer.of("Пользоватль с таким логином уже зарегистрирован");
            }
        } else {
            return BoxAnswer.of("Необходимо либо создать пр, либо быть ревьювером текущего ПР, чтобы пройти регистрацию");
        }
    }

}
