package dev.struchkov.bot.gitlab.telegram.config;

import dev.struchkov.bot.gitlab.telegram.service.ReplaceUrlLocalhost;
import dev.struchkov.bot.gitlab.telegram.unit.MenuConfig;
import dev.struchkov.bot.gitlab.telegram.unit.command.AnswerNoteUnit;
import dev.struchkov.bot.gitlab.telegram.unit.command.DeleteMessageUnit;
import dev.struchkov.bot.gitlab.telegram.unit.command.DisableNotifyMrUnit;
import dev.struchkov.bot.gitlab.telegram.unit.command.DisableNotifyThreadUnit;
import dev.struchkov.bot.gitlab.telegram.unit.command.EnableProjectNotify;
import dev.struchkov.bot.gitlab.telegram.unit.flow.InitSettingFlow;
import dev.struchkov.godfather.main.core.unit.TypeUnit;
import dev.struchkov.godfather.main.domain.content.Mail;
import dev.struchkov.godfather.simple.context.service.EventHandler;
import dev.struchkov.godfather.simple.context.service.PersonSettingService;
import dev.struchkov.godfather.simple.context.service.UnitPointerService;
import dev.struchkov.godfather.simple.core.action.cmd.RollBackCmdAction;
import dev.struchkov.godfather.simple.core.provider.StoryLineHandler;
import dev.struchkov.godfather.simple.core.service.PersonSettingServiceImpl;
import dev.struchkov.godfather.simple.core.service.StorylineContextMapImpl;
import dev.struchkov.godfather.simple.core.service.StorylineMailService;
import dev.struchkov.godfather.simple.core.service.StorylineService;
import dev.struchkov.godfather.simple.core.service.UnitPointerServiceImpl;
import dev.struchkov.godfather.simple.data.StorylineContext;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author upagge [30.01.2020]
 */
@Configuration
@EnableScheduling
public class TelegramBotConfig {

    @Bean("messageExecutorService")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(3);
    }

    @Bean
    public StorylineContext storylineContext() {
        return new StorylineContextMapImpl();
    }

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
            InitSettingFlow unitConfig,
            AnswerNoteUnit commandUnit,
            DeleteMessageUnit deleteMessageUnit,
            DisableNotifyMrUnit disableNotifyMrUnit,
            DisableNotifyThreadUnit disableNotifyThreadUnit,
            EnableProjectNotify enableProjectNotify
    ) {
        final List<Object> config = List.of(menuConfig, unitConfig, commandUnit, deleteMessageUnit, disableNotifyMrUnit,
                disableNotifyThreadUnit, enableProjectNotify);

        return new StorylineMailService(
                unitPointerService,
                new StorylineMapRepository(),
                config
        );
    }

    @Bean
    public MailAutoresponderTelegram messageAutoresponderTelegram(
            @Qualifier("messageExecutorService") ExecutorService executorService,
            TelegramSending sending,
            PersonSettingService personSettingService,

            StorylineService<Mail> storylineService
    ) {
        final MailAutoresponderTelegram autoresponder = new MailAutoresponderTelegram(
                sending, personSettingService, storylineService
        );
        autoresponder.initActionUnit(TypeUnit.BACK_CMD, new RollBackCmdAction<>(storylineService));
        autoresponder.setExecutorService(executorService);
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
