package org.sadtech.bot.gitlab.teamcity.core.repository;

import lombok.NonNull;
import org.sadtech.bot.gitlab.teamcity.core.domain.entity.BuildShort;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;

import java.util.Set;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
public interface BuildShortRepository extends SimpleManagerRepository<BuildShort, Long> {

    Set<Long> exists(@NonNull Set<Long> buildIds);

}
