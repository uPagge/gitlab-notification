package dev.struchkov.bot.gitlab.telegram.config;

import dev.struchkov.bot.gitlab.telegram.service.ReplaceUrlLocalhost;
import dev.struchkov.bot.gitlab.telegram.unit.MenuConfig;
import dev.struchkov.bot.gitlab.telegram.unit.UnitConfig;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.context.service.EventHandler;
import dev.struchkov.godfather.simple.context.service.PersonSettingService;
import dev.struchkov.godfather.simple.context.service.UnitPointerService;
import dev.struchkov.godfather.simple.core.provider.StoryLineHandler;
import dev.struchkov.godfather.simple.core.service.PersonSettingServiceImpl;
import dev.struchkov.godfather.simple.core.service.StorylineMailService;
import dev.struchkov.godfather.simple.core.service.StorylineService;
import dev.struchkov.godfather.simple.core.service.UnitPointerServiceImpl;
import dev.struchkov.godfather.simple.data.repository.impl.PersonSettingLocalRepository;
import dev.struchkov.godfather.simple.data.repository.impl.StorylineMapRepository;
import dev.struchkov.godfather.simple.data.repository.impl.UnitPointLocalRepository;
import dev.struchkov.godfather.telegram.domain.config.ProxyConfig;
import dev.struchkov.godfather.telegram.domain.config.TelegramConnectConfig;
import dev.struchkov.godfather.telegram.main.context.TelegramConnect;
import dev.struchkov.godfather.telegram.simple.consumer.EventDistributorService;
import dev.struchkov.godfather.telegram.simple.context.service.EventDistributor;
import dev.struchkov.godfather.telegram.simple.context.service.TelegramSending;
import dev.struchkov.godfather.telegram.simple.core.MailAutoresponderTelegram;
import dev.struchkov.godfather.telegram.simple.core.TelegramConnectBot;
import dev.struchkov.godfather.telegram.simple.core.service.SenderMapRepository;
import dev.struchkov.godfather.telegram.simple.sender.TelegramSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

/**
 * @author upagge [30.01.2020]
 */
@Configuration
@EnableScheduling
public class TelegramBotConfig {

    @Bean
    public UnitPointerService unitPointerService() {
        return new UnitPointerServiceImpl(new UnitPointLocalRepository());
    }

    @Bean
    public PersonSettingService personSettingService() {
        return new PersonSettingServiceImpl(new PersonSettingLocalRepository());
    }

    @Bean
    public StorylineService<Mail> storylineService(
            UnitPointerService unitPointerService,

            MenuConfig menuConfig,
            UnitConfig unitConfig
    ) {
        final List<Object> config = List.of(menuConfig, unitConfig);

        return new StorylineMailService(
                unitPointerService,
                new StorylineMapRepository(),
                config
        );
    }

    @Bean
    public MailAutoresponderTelegram messageAutoresponderTelegram(
            TelegramSending sending,
            PersonSettingService personSettingService,

            StorylineService<Mail> mailStorylineService
    ) {
        final MailAutoresponderTelegram autoresponder = new MailAutoresponderTelegram(
                sending, personSettingService, mailStorylineService
        );
        return autoresponder;
    }

    @Bean
    public TelegramSending sending(
            TelegramConnect telegramConnect,
            ReplaceUrlLocalhost replaceUrlLocalhost
    ) {
        final TelegramSender telegramSender = new TelegramSender(telegramConnect);
        telegramSender.addPreSendProcess(replaceUrlLocalhost);
        telegramSender.setSenderRepository(new SenderMapRepository());
        return telegramSender;
    }

    @Bean
    public TelegramConnectBot telegramConnect(TelegramConnectConfig telegramConfig) {
        return new TelegramConnectBot(telegramConfig);
    }

    @Bean
    @ConfigurationProperties("telegram-proxy-config")
    @ConditionalOnProperty(prefix = "telegram-config.proxy-config.enable", value = "true")
    public ProxyConfig proxyConfig() {
        return new ProxyConfig();
    }

    @Bean
    @ConfigurationProperties("telegram-config")
    public TelegramConnectConfig telegramConfig() {
        return new TelegramConnectConfig();
    }

    @Bean
    public StoryLineHandler storyLineHandler(
            MailAutoresponderTelegram mailAutoresponderTelegram
    ) {
        return new StoryLineHandler(mailAutoresponderTelegram);
    }

    @Bean
    public EventDistributor eventDistributor(
            TelegramConnectBot telegramConnect,
            List<EventHandler> eventProviders
    ) {
        return new EventDistributorService(telegramConnect, eventProviders);
    }

}
