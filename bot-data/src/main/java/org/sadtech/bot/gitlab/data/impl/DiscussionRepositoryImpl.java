package org.sadtech.bot.gitlab.data.impl;

import org.sadtech.bot.gitlab.context.domain.entity.Discussion;
import org.sadtech.bot.gitlab.context.repository.DiscussionRepository;
import org.sadtech.haiti.database.repository.manager.AbstractSimpleManagerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * // TODO: 11.02.2021 Добавить описание.
 *
 * @author upagge 11.02.2021
 */
@Repository
public class DiscussionRepositoryImpl extends AbstractSimpleManagerRepository<Discussion, String> implements DiscussionRepository {

    public DiscussionRepositoryImpl(JpaRepository<Discussion, String> jpaRepository) {
        super(jpaRepository);
    }

}
