package org.sadtech.bot.gitlab.teamcity.core.repository;

import lombok.NonNull;
import org.sadtech.bot.gitlab.teamcity.core.domain.entity.TeamcityProject;
import org.sadtech.haiti.context.repository.SimpleManagerRepository;

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
