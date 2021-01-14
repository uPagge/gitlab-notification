package org.sadtech.bot.gitlab.app.config;

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

}
