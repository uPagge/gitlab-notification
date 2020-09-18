package org.sadtech.bot.vcs.telegram.config;

import org.sadtech.autoresponder.repository.UnitPointerRepository;
import org.sadtech.autoresponder.repository.UnitPointerRepositoryMap;
import org.sadtech.bot.vcs.telegram.service.ReplaceUrlLocalhost;
import org.sadtech.social.bot.domain.unit.AnswerCheck;
import org.sadtech.social.core.domain.content.Mail;
import org.sadtech.social.core.repository.impl.local.MailRepositoryList;
import org.sadtech.social.core.service.MailService;
import org.sadtech.social.core.service.MessageService;
import org.sadtech.social.core.service.impl.MailServiceImpl;
import org.sadtech.social.core.service.sender.Sending;
import org.sadtech.telegram.bot.autoresponder.MessageAutoresponderTelegram;
import org.sadtech.telegram.bot.config.TelegramPollingConfig;
import org.sadtech.telegram.bot.listen.EventDistributor;
import org.sadtech.telegram.bot.listen.EventDistributorImpl;
import org.sadtech.telegram.bot.listen.TelegramConnect;
import org.sadtech.telegram.bot.listen.TelegramSender;
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
            AnswerCheck regCheck,
            Sending sending,
            MessageService<Mail> messageService,
            UnitPointerRepository unitPointerRepository
    ) {
        return new MessageAutoresponderTelegram(
                Collections.singleton(regCheck),
                sending,
                messageService,
                unitPointerRepository
        );
    }

    @Bean
    public Sending sending(
            TelegramConnect telegramConnect,
            ReplaceUrlLocalhost replaceUrlLocalhost
    ) {
        final TelegramSender telegramSender = new TelegramSender(telegramConnect);
        telegramSender.setSendPreProcessing(replaceUrlLocalhost);
        return telegramSender;
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
