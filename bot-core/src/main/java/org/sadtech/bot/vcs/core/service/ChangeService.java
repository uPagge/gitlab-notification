package org.sadtech.bot.vcs.core.service;

import org.sadtech.bot.vcs.core.domain.change.Change;

import java.util.List;

/**
 * Сервис по работе с изменениями в битбакете.
 *
 * @author upagge
 * @see Change
 */
public interface ChangeService {

    <T extends Change> void save(T task);

    /**
     * Позволяет получить новые изменения.
     */
    List<Change> getNew();

}
