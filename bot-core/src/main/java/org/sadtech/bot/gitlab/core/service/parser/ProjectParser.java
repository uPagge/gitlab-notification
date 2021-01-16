package org.sadtech.bot.gitlab.core.service.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.gitlab.context.domain.entity.Person;
import org.sadtech.bot.gitlab.context.domain.entity.Project;
import org.sadtech.bot.gitlab.context.service.PersonService;
import org.sadtech.bot.gitlab.context.service.ProjectService;
import org.sadtech.bot.gitlab.core.config.properties.GitlabProperty;
import org.sadtech.bot.gitlab.core.config.properties.PersonProperty;
import org.sadtech.bot.gitlab.sdk.domain.PersonJson;
import org.sadtech.bot.gitlab.sdk.domain.ProjectJson;
import org.sadtech.haiti.context.domain.ExistsContainer;
import org.sadtech.haiti.context.exception.ConvertException;
import org.sadtech.haiti.utils.network.HttpHeader;
import org.sadtech.haiti.utils.network.HttpParse;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.sadtech.haiti.utils.network.HttpParse.ACCEPT;
import static org.sadtech.haiti.utils.network.HttpParse.AUTHORIZATION;
import static org.sadtech.haiti.utils.network.HttpParse.BEARER;

/**
 * // TODO: 14.01.2021 Добавить описание.
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

    public void parseAllPrivateProject() {
        parseProjects(PRIVATE);
    }

    public void parseAllProjectOwner() {
        parseProjects(OWNER);
    }

    private void parseProjects(String param) {
        int page = 0;
        List<ProjectJson> projectJsons = getProjectJsons(page, param);

        while (!projectJsons.isEmpty()) {

            final Set<Long> jsonIds = projectJsons.stream()
                    .map(ProjectJson::getId)
                    .collect(Collectors.toSet());

            createNewPersons(projectJsons);

            final ExistsContainer<Project, Long> existsContainer = projectService.existsById(jsonIds);
            final List<Project> newProjects = projectJsons.stream()
                    .filter(json -> existsContainer.getIdNoFound().contains(json.getId()))
                    .map(json -> conversionService.convert(json, Project.class))
                    .collect(Collectors.toList());

            if (!newProjects.isEmpty()) {
                projectService.createAll(newProjects);
            }

            projectJsons = getProjectJsons(++page, param);
        }
    }

    private void createNewPersons(List<ProjectJson> projectJsons) {
        final Set<Long> jsonIds = projectJsons.stream()
                .map(ProjectJson::getCreatorId)
                .collect(Collectors.toSet());

        final ExistsContainer<Person, Long> existsContainer = personService.existsById(jsonIds);

        if (!existsContainer.isAllFound()) {
            final Collection<Long> notFoundId = existsContainer.getIdNoFound();

            final List<Person> newPersons = notFoundId.stream()
                    .map(
                            userId -> HttpParse.request(gitlabProperty.getUsersUrl() + "/" + userId)
                                    .header(ACCEPT)
                                    .header(AUTHORIZATION, BEARER + personProperty.getToken())
                                    .execute(PersonJson.class)
                                    .map(json -> conversionService.convert(json, Person.class)).orElseThrow(() -> new ConvertException("Ошибка преобразования нового пользователя"))
                    ).collect(Collectors.toList());

            personService.createAll(newPersons);

        }
    }

    private List<ProjectJson> getProjectJsons(int page, String... params) {
        String param = String.join("", params);
        final String url = MessageFormat.format(gitlabProperty.getUrlProject(), page);
        return HttpParse.request(url + param)
                .header(ACCEPT)
                .header(HttpHeader.of(AUTHORIZATION, BEARER + personProperty.getToken()))
                .executeList(ProjectJson.class);
    }

}
