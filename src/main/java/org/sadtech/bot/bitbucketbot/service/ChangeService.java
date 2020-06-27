package org.sadtech.bot.bitbucketbot.service;

import lombok.NonNull;
import org.sadtech.bot.bitbucketbot.domain.change.Change;

import java.util.List;

/**
 * Сервис по работе с изменениями в битбакете.
 *
 * @author upagge
 * @see Change
 */
public interface ChangeService {

    /**
     * Позволяет добавить новое изменение в хранилище
     *
     * @param change Объект, который содержит изменения
     */
    void add(@NonNull Change change);

    /**
     * Позволяет получить новые изменения.
     */
    List<Change> getNew();

}
