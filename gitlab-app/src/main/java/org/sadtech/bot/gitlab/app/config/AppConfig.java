package org.sadtech.bot.gitlab.app.config;

import org.sadtech.bot.gitlab.context.domain.PersonInformation;
import org.sadtech.bot.gitlab.context.exception.NotFoundException;
import org.sadtech.bot.gitlab.core.config.properties.GitlabProperty;
import org.sadtech.bot.gitlab.core.config.properties.PersonProperty;
import org.sadtech.haiti.utils.network.HttpParse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.sadtech.haiti.utils.network.HttpParse.ACCEPT;
import static org.sadtech.haiti.utils.network.HttpParse.AUTHORIZATION;
import static org.sadtech.haiti.utils.network.HttpParse.BEARER;

/**
 * Общий файл настройки всего приложения.
 *
 * @author upagge
 */
@Configuration
@EnableScheduling
public class AppConfig {

    /**
     * Отвечает за работу шедулеров в паралельном режиме
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(12);
        return taskScheduler;
    }

    @Bean
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(3);
    }

    @Bean
    public ConversionService conversionService(Converter... converters) {
        final DefaultConversionService defaultConversionService = new DefaultConversionService();
        Arrays.stream(converters).forEach(defaultConversionService::addConverter);
        return defaultConversionService;
    }

    @Bean
    public PersonInformation personInformation(
            PersonProperty personProperty,
            GitlabProperty gitlabProperty
    ) {
        final PersonInformation personInformation = HttpParse.request(gitlabProperty.getUserUrl())
                .header(ACCEPT)
                .header(AUTHORIZATION, BEARER + personProperty.getToken())
                .execute(PersonInformation.class)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        personInformation.setTelegramId(personProperty.getTelegramId());
        return personInformation;
    }

}
