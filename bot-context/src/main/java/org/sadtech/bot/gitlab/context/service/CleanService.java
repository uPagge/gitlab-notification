package org.sadtech.bot.gitlab.context.service;

/**
 * // TODO: 08.02.2021 Добавить описание.
 *
 * @author upagge 08.02.2021
 */
public interface CleanService {

    void cleanMergedPullRequests();

    void cleanOldPipelines();

}
