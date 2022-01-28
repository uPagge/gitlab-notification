package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import dev.struchkov.bot.gitlab.context.repository.ProjectRepository;
import dev.struchkov.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author upagge 14.01.2021
 */
@Repository
public class ProjectRepositoryImpl extends AbstractSimpleManagerRepository<Project, Long> implements ProjectRepository {

    public ProjectRepositoryImpl(JpaRepository<Project, Long> jpaRepository) {
        super(jpaRepository);
    }

}
