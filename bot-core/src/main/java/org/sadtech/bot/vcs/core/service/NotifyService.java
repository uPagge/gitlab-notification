package org.sadtech.bot.vcs.core.service;

import org.sadtech.bot.vcs.core.domain.notify.Notify;

import java.util.List;

/**
 * Сервис по работе с изменениями в битбакете.
 *
 * @author upagge
 * @see Notify
 */
public interface NotifyService {

    <T extends Notify> void save(T notify);

    /**
     * Позволяет получить новые изменения.
     */
    List<Notify> getNew();

}
