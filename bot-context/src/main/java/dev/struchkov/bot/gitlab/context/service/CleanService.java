package dev.struchkov.bot.gitlab.context.service;

/**
 * <p>Контракт очистки хранилища.</p>
 * <p>Так как все что мы получаем от гитлаба сохраняется в БД, иногда нужно удалять устаревшие данные. Например, MR, которые уже были давно вмержены.</p>
 *
 * @author upagge 08.02.2021
 */
public interface CleanService {

    /**
     * <p>Удаляет старые MR.</p>
     * <p>По умолчанию старыми считаются те, которые закрыты или вмержены</p>
     */
    void cleanOldMergedRequests();


}
