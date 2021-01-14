package org.sadtech.bot.gitlab.data.impl;

import org.sadtech.bot.gitlab.context.domain.entity.Project;
import org.sadtech.bot.gitlab.context.repository.ProjectRepository;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * // TODO: 14.01.2021 Добавить описание.
 *
 * @author upagge 14.01.2021
 */
@Repository
public class ProjectRepositoryImpl extends AbstractSimpleManagerRepository<Project, Long> implements ProjectRepository {

    public ProjectRepositoryImpl(JpaRepository<Project, Long> jpaRepository) {
        super(jpaRepository);
    }

}
