package dev.struchkov.bot.gitlab.data.jpa;

import dev.struchkov.bot.gitlab.context.domain.IdAndStatusIssue;
import dev.struchkov.bot.gitlab.context.domain.IssueState;
import dev.struchkov.bot.gitlab.context.domain.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

/**
 * @author Dmitry Sheyko [24.01.2023]
 */
public interface IssueJpaRepository extends JpaRepository<Issue, Long> {

    @Query("SELECT new dev.struchkov.bot.gitlab.context.domain.IdAndStatusIssue(i.id, i.twoId, i.projectId, i.state) FROM Issue i WHERE i.state IN :states")
    Set<IdAndStatusIssue> findAllIdByStateIn(@Param("states") Set<IssueState> states);

    void deleteAllByStateIn(Set<IssueState> states);

}