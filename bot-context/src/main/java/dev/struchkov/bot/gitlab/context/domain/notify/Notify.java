package dev.struchkov.bot.gitlab.context.domain.notify;

import dev.struchkov.bot.gitlab.context.service.AppSettingService;

public interface Notify {

    String generateMessage(AppSettingService appSettingService);

}
