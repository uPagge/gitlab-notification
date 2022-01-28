package dev.struchkov.bot.gitlab.context.repository;

import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import dev.struchkov.haiti.context.repository.SimpleManagerRepository;

/**
 * @author upagge 14.01.2021
 */
public interface ProjectRepository extends SimpleManagerRepository<Project, Long> {

}
