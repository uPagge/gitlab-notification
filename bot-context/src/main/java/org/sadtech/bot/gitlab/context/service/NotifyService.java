package org.sadtech.bot.gitlab.context.service;

import org.sadtech.bot.gitlab.context.domain.notify.Notify;

/**
 * Сервис по работе с изменениями в битбакете.
 *
 * @author upagge
 * @see Notify
 */
public interface NotifyService {

    <T extends Notify> void send(T notify);

}
