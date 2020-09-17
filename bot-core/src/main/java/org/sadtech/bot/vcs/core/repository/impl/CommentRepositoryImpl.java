package org.sadtech.bot.vcs.core.repository.impl;

import lombok.NonNull;
import org.sadtech.basic.database.repository.manager.AbstractSimpleManagerRepository;
import org.sadtech.bot.vcs.core.domain.entity.Comment;
import org.sadtech.bot.vcs.core.repository.CommentRepository;
import org.sadtech.bot.vcs.core.repository.jpa.CommentRepositoryJpa;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * // TODO: 08.09.2020 Добавить описание.
 *
 * @author upagge 08.09.2020
 */
@Repository
public class CommentRepositoryImpl extends AbstractSimpleManagerRepository<Comment, Long> implements CommentRepository {

    private final CommentRepositoryJpa repositoryJpa;

    public CommentRepositoryImpl(CommentRepositoryJpa repositoryJpa) {
        super(repositoryJpa);
        this.repositoryJpa = repositoryJpa;
    }

    @Override
    public Optional<Comment> findFirstByOrderByIdDesc() {
        return repositoryJpa.findFirstByOrderByIdDesc();
    }

    @Override
    public List<Comment> findByCreateDateBetween(LocalDateTime dateFrom, LocalDateTime dateTo) {
        return repositoryJpa.findByCreateDateBetween(dateFrom, dateTo);
    }

    @Override
    public List<Comment> findAllById(@NonNull Set<Long> ids) {
        return repositoryJpa.findAllById(ids);
    }

    @Override
    public Set<Long> existsById(Set<Long> ids) {
        return repositoryJpa.existsAllById(ids);
    }

}
