package org.sadtech.bot.bitbucketbot.service.parser;

/**
 * // TODO: 06.09.2020 Добавить описание.
 *
 * @author upagge 06.09.2020
 */
public interface PullRequestParser {

    /**
     * Проверка старых ПР на изменение.
     */
    void parsingOldPullRequest();

    /**
     * Извлекает новые ПР.
     */
    void parsingNewPullRequest();

}
