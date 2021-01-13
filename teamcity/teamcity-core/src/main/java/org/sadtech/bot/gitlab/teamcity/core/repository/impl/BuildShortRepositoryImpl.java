package org.sadtech.bot.gitlab.teamcity.core.repository.impl;

import lombok.NonNull;
import org.sadtech.bot.gitlab.teamcity.core.domain.entity.BuildShort;
import org.sadtech.bot.gitlab.teamcity.core.repository.BuildShortRepository;
import org.sadtech.bot.gitlab.teamcity.core.repository.jpa.BuildShortJpaRepository;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Repository
public class BuildShortRepositoryImpl extends AbstractSimpleManagerRepository<BuildShort, Long> implements BuildShortRepository {

    private final BuildShortJpaRepository jpaRepository;

    public BuildShortRepositoryImpl(BuildShortJpaRepository jpaRepository) {
        super(jpaRepository);
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Set<Long> exists(@NonNull Set<Long> buildIds) {
        return jpaRepository.existsByIds(buildIds);
    }

}
