package org.sadtech.bot.vcs.teamcity.core.repository;

import lombok.NonNull;
import org.sadtech.basic.context.repository.SimpleManagerRepository;
import org.sadtech.bot.vcs.teamcity.core.domain.entity.TeamcityProject;

import java.util.List;
import java.util.Set;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
public interface TeamcityProjectRepository extends SimpleManagerRepository<TeamcityProject, String> {

    List<String> exists(@NonNull Set<String> projectIds);

}
