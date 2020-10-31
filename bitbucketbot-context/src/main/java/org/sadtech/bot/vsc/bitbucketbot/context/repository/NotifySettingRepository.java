package org.sadtech.bot.vsc.bitbucketbot.context.repository;

import lombok.NonNull;
import org.sadtech.basic.context.repository.SimpleManagerRepository;
import org.sadtech.bot.vsc.bitbucketbot.context.domain.entity.NotifySetting;

import java.util.Set;

/**
 * // TODO: 20.09.2020 Добавить описание.
 *
 * @author upagge 20.09.2020
 */
public interface NotifySettingRepository extends SimpleManagerRepository<NotifySetting, String> {

    boolean isNotification(@NonNull String login);

    Set<String> isNotification(@NonNull Set<String> logins);

}
