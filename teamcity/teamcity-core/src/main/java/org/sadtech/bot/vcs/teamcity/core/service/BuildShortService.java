package org.sadtech.bot.vcs.teamcity.core.service;

import org.sadtech.basic.context.service.SimpleManagerService;
import org.sadtech.bot.vcs.teamcity.core.domain.entity.BuildShort;

import java.util.Set;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
public interface BuildShortService extends SimpleManagerService<BuildShort, Long> {

    Set<Long> exists(Set<Long> buildIds);

}
