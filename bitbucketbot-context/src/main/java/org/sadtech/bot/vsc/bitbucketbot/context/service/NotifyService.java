package org.sadtech.bot.vsc.bitbucketbot.context.service;

import lombok.NonNull;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.NotifySetting;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.notify.Notify;

import java.util.Optional;

/**
 * Сервис по работе с изменениями в битбакете.
 *
 * @author upagge
 * @see Notify
 */
public interface NotifyService {

    <T extends Notify> void send(T notify);

    /**
     * Сохранить настройки уведомлений
     */
    void saveSettings(@NonNull NotifySetting setting);

    /**
     * Получить настройки уведомлений по логину.
     *
     * @param login Логин пользователя
     */
    Optional<NotifySetting> getSetting(@NonNull String login);

}
