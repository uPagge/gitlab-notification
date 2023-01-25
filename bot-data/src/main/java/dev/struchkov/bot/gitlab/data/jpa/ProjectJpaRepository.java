package dev.struchkov.bot.gitlab.data.jpa;

import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

/**
 * @author upagge 14.01.2021
 */
public interface ProjectJpaRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p.id FROM Project p WHERE p.processing = true")
    Set<Long> findAllIdByProcessingEnableTrue();

    @Query("SELECT p.name FROM Project p WHERE p.id = :projectId")
    Optional<String> findProjectNameById(@Param("projectId") Long projectId);

    @Query("SELECT p.id FROM Project p")
    Set<Long> findAllIds();

    @Modifying
    @Query("UPDATE Project p SET p.notification = :enable WHERE p.id in :projectIds")
    void notification(@Param("enable") boolean enable, @Param("projectIds") Set<Long> projectIds);

    @Modifying
    @Query("UPDATE Project p SET p.processing = :enable WHERE p.id in :projectIds")
    void processing(@Param("enable") boolean enable, @Param("projectIds") Set<Long> projectIds);

}
