package dev.struchkov.bot.gitlab.telegram.config;

import dev.struchkov.bot.gitlab.telegram.service.ReplaceUrlLocalhost;
import dev.struchkov.godfather.context.domain.content.Mail;
import dev.struchkov.godfather.context.repository.impl.local.MailRepositoryList;
import dev.struchkov.godfather.context.service.MailService;
import dev.struchkov.godfather.context.service.MessageService;
import dev.struchkov.godfather.context.service.impl.MailServiceImpl;
import dev.struchkov.godfather.context.service.sender.Sending;
import dev.struchkov.godfather.core.domain.unit.AnswerCheck;
import dev.struchkov.godfather.telegram.autoresponder.MessageAutoresponderTelegram;
import dev.struchkov.godfather.telegram.config.TelegramPollingConfig;
import dev.struchkov.godfather.telegram.listen.EventDistributor;
import dev.struchkov.godfather.telegram.listen.EventDistributorImpl;
import dev.struchkov.godfather.telegram.listen.TelegramConnect;
import dev.struchkov.godfather.telegram.listen.TelegramSender;
import org.sadtech.autoresponder.repository.UnitPointerRepository;
import org.sadtech.autoresponder.repository.UnitPointerRepositoryMap;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Collections;

/**
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
            UnitPointerRepository unitPointerRepository,
            AnswerCheck checkFirstStart
    ) {
        return new MessageAutoresponderTelegram(
                Collections.singleton(checkFirstStart),
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
