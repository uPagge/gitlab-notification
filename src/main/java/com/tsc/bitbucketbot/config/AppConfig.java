package com.tsc.bitbucketbot.config;

import org.sadtech.autoresponder.repository.UnitPointerRepository;
import org.sadtech.autoresponder.repository.UnitPointerRepositoryMap;
import org.sadtech.social.bot.domain.unit.AnswerCheck;
import org.sadtech.social.bot.service.action.AnswerSaveAction;
import org.sadtech.social.core.domain.content.Mail;
import org.sadtech.social.core.repository.impl.local.MailRepositoryList;
import org.sadtech.social.core.service.MailService;
import org.sadtech.social.core.service.MessageService;
import org.sadtech.social.core.service.impl.MailServiceImpl;
import org.sadtech.social.core.service.sender.Sending;
import org.sadtech.telegram.bot.TelegramConfig;
import org.sadtech.telegram.bot.autoresponder.MessageAutoresponderTelegram;
import org.sadtech.telegram.bot.listen.EventDistributor;
import org.sadtech.telegram.bot.listen.EventDistributorImpl;
import org.sadtech.telegram.bot.listen.TelegramConnect;
import org.sadtech.telegram.bot.listen.TelegramSender;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Collections;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Configuration
@EnableScheduling
public class AppConfig {

    @Bean
    public MailService messageService() {
        return new MailServiceImpl(new MailRepositoryList());
    }

    @Bean
    public UnitPointerRepository unitPointerRepository() {
        return new UnitPointerRepositoryMap();
    }

    @Bean
    public MessageAutoresponderTelegram messageAutoresponderTelegram(
            AnswerCheck checkMenu,
            Sending sending,
            MessageService<Mail> messageService,
            UnitPointerRepository unitPointerRepository
    ) {
        final MessageAutoresponderTelegram messageAutoresponderTelegram = new MessageAutoresponderTelegram(
                Collections.singleton(checkMenu),
                sending,
                messageService,
                unitPointerRepository
        );
        messageAutoresponderTelegram.initSaveAction(new AnswerSaveAction<>());
        return messageAutoresponderTelegram;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(6);
        return taskScheduler;
    }

    @Bean
    public Sending sending(
            TelegramConnect telegramConnect
    ) {
        return new TelegramSender(telegramConnect);
    }

    @Bean
    public TelegramConnect telegramConnect(
            TelegramConfig telegramConfig
    ) {
        return new TelegramConnect(telegramConfig);
    }

    @Bean
    @ConfigurationProperties("bitbucketbot.telegram")
    public TelegramConfig telegramConfig() {
        return new TelegramConfig();
    }

    @Bean
    public EventDistributor eventDistributor(
            TelegramConnect telegramConnect,
            MailService mailService
    ) {
        return new EventDistributorImpl(telegramConnect, mailService);
    }

}
