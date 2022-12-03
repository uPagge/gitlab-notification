package dev.struchkov.bot.gitlab.data.impl;

import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import dev.struchkov.bot.gitlab.context.repository.ProjectRepository;
import dev.struchkov.bot.gitlab.data.jpa.ProjectJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author upagge 14.01.2021
 */
@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {

    private final ProjectJpaRepository jpaRepository;

    @Override
    public Project save(Project project) {
        return jpaRepository.save(project);
    }

    @Override
    public Optional<Project> findById(Long projectId) {
        return jpaRepository.findById(projectId);
    }

    @Override
    public List<Project> findAllById(Set<Long> projectIds) {
        return jpaRepository.findAllById(projectIds);
    }

    @Override
    public boolean existById(Long projectId) {
        return jpaRepository.existsById(projectId);
    }

    @Override
    public Page<Project> findAllById(Pageable pagination) {
        return jpaRepository.findAll(pagination);
    }

}
