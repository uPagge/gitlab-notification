package dev.struchkov.bot.gitlab.core.service.parser;

import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import dev.struchkov.bot.gitlab.context.service.PersonService;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.bot.gitlab.core.config.properties.PersonProperty;
import dev.struchkov.bot.gitlab.core.utils.StringUtils;
import dev.struchkov.bot.gitlab.sdk.domain.PersonJson;
import dev.struchkov.bot.gitlab.sdk.domain.ProjectJson;
import dev.struchkov.haiti.utils.network.HttpParse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.context.exception.ConvertException.convertException;
import static dev.struchkov.haiti.utils.network.HttpParse.ACCEPT;

/**
 * Парсер проектов.
 *
 * @author upagge 14.01.2021
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectParser {

    public static final String PRIVATE = "&visibility=private";
    public static final String OWNER = "&owned=true";

    private final ProjectService projectService;
    private final PersonService personService;

    private final ConversionService conversionService;

    private final GitlabProperty gitlabProperty;
    private final PersonProperty personProperty;

    @Transactional
    public void parseAllPrivateProject() {
        parseProjects(PRIVATE);
    }

    @Transactional
    public void parseAllProjectOwner() {
        parseProjects(OWNER);
    }

    private void parseProjects(String param) {
        int page = 1;
        List<ProjectJson> projectJsons = getProjectJsons(page, param);

        while (!projectJsons.isEmpty()) {

            final Set<Long> projectIds = projectJsons.stream()
                    .map(ProjectJson::getId)
                    .collect(Collectors.toUnmodifiableSet());

            createNewPersons(projectJsons);

            final ExistContainer<Project, Long> existContainer = projectService.existsById(projectIds);
            final List<Project> newProjects = projectJsons.stream()
                    .filter(json -> existContainer.getIdNoFound().contains(json.getId()))
                    .map(json -> conversionService.convert(json, Project.class))
                    .toList();

            if (!newProjects.isEmpty()) {
                projectService.createAll(newProjects);
            }

            projectJsons = getProjectJsons(++page, param);
        }
    }

    public void parseByUrl(String projectUrl) {
        final ProjectJson projectJson = HttpParse.request(projectUrl)
                .header(ACCEPT)
                .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                .execute(ProjectJson.class)
                .orElseThrow(convertException("Ошибка получения проекта"));
        if (!projectService.existsById(projectJson.getId())) {
            createNewPersons(List.of(projectJson));

            final Project newProject = conversionService.convert(projectJson, Project.class);
            projectService.create(newProject);
        }
    }

    private void createNewPersons(List<ProjectJson> projectJsons) {
        final Set<Long> personCreatorId = projectJsons.stream()
                .map(ProjectJson::getCreatorId)
                .collect(Collectors.toSet());

        final ExistContainer<Person, Long> existContainer = personService.existsById(personCreatorId);

        if (!existContainer.isAllFound()) {
            final Collection<Long> notFoundId = existContainer.getIdNoFound();

            final List<Person> newPersons = notFoundId.stream()
                    .map(
                            userId -> HttpParse.request(gitlabProperty.getUsersUrl() + "/" + userId)
                                    .header(ACCEPT)
                                    .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                                    .execute(PersonJson.class)
                                    .map(json -> conversionService.convert(json, Person.class))
                                    .orElseThrow(convertException("Ошибка преобразования нового пользователя"))
                    ).toList();

            personService.createAll(newPersons);
        }
    }

    private List<ProjectJson> getProjectJsons(int page, String... params) {
        String param = String.join("", params);
        final String url = MessageFormat.format(gitlabProperty.getUrlProject(), page);
        return HttpParse.request(url + param)
                .header(ACCEPT)
                .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                .executeList(ProjectJson.class);
    }

}
