package dev.struchkov.bot.gitlab.data.jpa;

import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

/**
 * @author upagge 14.01.2021
 */
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p.id FROM Project p")
    Set<Long> findAllIds();

    @Query("SELECT p.name FROM Project p WHERE p.id = :projectId")
    Optional<String> findProjectNameById(@Param("projectId") Long projectId);

}
