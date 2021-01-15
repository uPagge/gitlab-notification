package org.sadtech.bot.gitlab.telegram.config;

import org.sadtech.autoresponder.repository.UnitPointerRepository;
import org.sadtech.autoresponder.repository.UnitPointerRepositoryMap;
import org.sadtech.bot.godfather.telegram.autoresponder.MessageAutoresponderTelegram;
import org.sadtech.bot.godfather.telegram.config.TelegramPollingConfig;
import org.sadtech.bot.godfather.telegram.listen.EventDistributor;
import org.sadtech.bot.godfather.telegram.listen.EventDistributorImpl;
import org.sadtech.bot.godfather.telegram.listen.TelegramConnect;
import org.sadtech.bot.godfather.telegram.listen.TelegramSender;
import org.sadtech.social.bot.domain.unit.AnswerText;
import org.sadtech.social.core.domain.content.Mail;
import org.sadtech.social.core.repository.impl.local.MailRepositoryList;
import org.sadtech.social.core.service.MailService;
import org.sadtech.social.core.service.MessageService;
import org.sadtech.social.core.service.impl.MailServiceImpl;
import org.sadtech.social.core.service.sender.Sending;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Collections;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Configuration
@EnableScheduling
public class TelegramBotConfig {

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
            Sending sending,
            MessageService<Mail> messageService,
            UnitPointerRepository unitPointerRepository
    ) {
        return new MessageAutoresponderTelegram(
                Collections.singleton(AnswerText.of("TEST")),
                sending,
                messageService,
                unitPointerRepository
        );
    }

    @Bean
    public Sending sending(
            TelegramConnect telegramConnect
    ) {
        return new TelegramSender(telegramConnect);
    }

    @Bean
    public TelegramConnect telegramConnect(TelegramPollingConfig telegramConfig) {
        return new TelegramConnect(telegramConfig);
    }

    @Bean
    @ConfigurationProperties("telegram-config")
    public TelegramPollingConfig telegramConfig() {
        return new TelegramPollingConfig();
    }

    @Bean
    public EventDistributor eventDistributor(
            TelegramConnect telegramConnect,
            MailService mailService
    ) {
        return new EventDistributorImpl(telegramConnect, mailService);
    }

}
