package dev.struchkov.bot.gitlab.core.service.parser;

import dev.struchkov.bot.gitlab.context.domain.*;
import dev.struchkov.bot.gitlab.context.domain.entity.Issue;
import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.context.service.IssueService;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.bot.gitlab.core.config.properties.PersonProperty;
import dev.struchkov.bot.gitlab.core.service.parser.forktask.GetAllIssueForProjectTask;
import dev.struchkov.bot.gitlab.core.service.parser.forktask.GetSingleIssueTask;
import dev.struchkov.bot.gitlab.sdk.domain.IssueJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.concurrent.ForkJoinUtils.pullTaskResult;
import static dev.struchkov.haiti.utils.concurrent.ForkJoinUtils.pullTaskResults;

/**
 * @author Dmotry Sheyko [24.01.2023]
 */
@Slf4j
@Service
public class IssueParser {

    private static final Set<IssueState> OLD_STATUSES = Set.of(IssueState.OPENED, IssueState.CLOSED);

    private final GitlabProperty gitlabProperty;
    private final IssueService issueService;
    private final ProjectService projectService;
    private final ConversionService conversionService;
    private final PersonProperty personProperty;

    private final ForkJoinPool forkJoinPool;

    public IssueParser(
            GitlabProperty gitlabProperty,
            IssueService issueService,
            ProjectService projectService,
            ConversionService conversionService,
            PersonProperty personProperty,
            @Qualifier("parserPool") ForkJoinPool forkJoinPool
    ) {
        this.gitlabProperty = gitlabProperty;
        this.issueService = issueService;
        this.projectService = projectService;
        this.conversionService = conversionService;
        this.personProperty = personProperty;
        this.forkJoinPool = forkJoinPool;
    }

    public void  parsingOldIssue(){
        log.debug("Старт обработаки старых Issue");
        final Set<IdAndStatusIssue> existIds = issueService.getAllId(OLD_STATUSES);

        final List<Issue> newIssues = getOldIssues(existIds).stream()
                .map(issueJson -> {
                    final Issue newIssue = conversionService.convert(issueJson, Issue.class);
                    return newIssue;
                })
                .collect(Collectors.toList());

        if (checkNotEmpty(newIssues)) {
            personMapping(newIssues);
            issueService.updateAll(newIssues);
        }
        log.debug("Конец обработки старых Issue");
    }

    private List<IssueJson> getOldIssues(Set<IdAndStatusIssue> existIds) {
        final List<ForkJoinTask<Optional<IssueJson>>> tasks = existIds.stream()
                .map(
                        existId -> new GetSingleIssueTask(
                                gitlabProperty.getIssueUrl(),
                                existId.getProjectId(),
                                existId.getTwoId(),
                                personProperty.getToken()
                        )
                ).map(forkJoinPool::submit)
                .collect(Collectors.toList());

        return pullTaskResult(tasks).stream()
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }


    public void parsingNewIssue() {
        log.debug("Старт обработки новых Issue");

        /**
         * получаем через репозиторий список id всех проектов хранящихся в БД
         */
        final Set<Long> projectIds = projectService.getAllIds();

        /**
         * На основе id проекта, url для получения issues по id проекта и токена пользователя
         * выгружаем из GitLab список всех IssueJson. Получаем в многопоточном режиме.
         */
        final List<IssueJson> issueJsons = getIssues(projectIds);

        /**
         * Получаем id всех IssueJson загруженных из GitLab
         */
        if (checkNotEmpty(issueJsons)) {
            final Set<Long> jsonIds = issueJsons.stream()
                    .map(IssueJson::getId)
                    .collect(Collectors.toSet());

            final ExistContainer<Issue, Long> existContainer = issueService.existsById(jsonIds);
            log.trace("Из {} полученных MR не найдены в хранилище {}", jsonIds.size(), existContainer.getIdNoFound().size());
            if (!existContainer.isAllFound()) {
                final List<Issue> newIssues = issueJsons.stream()
                        .filter(json -> existContainer.getIdNoFound().contains(json.getId()))
                        .map(json -> {
                            final Issue issue = conversionService.convert(json, Issue.class);
                            return issue;
                        })
                        .toList();
                log.trace("Пачка новых issues обработана и отправлена на сохранение. Количество: {} шт.", newIssues.size());
                issueService.createAll(newIssues);
            }
        }
        log.debug("Конец обработки новых MR");
    }

    private List<IssueJson> getIssues(Set<Long> projectIds) {
        final List<ForkJoinTask<List<IssueJson>>> tasks = projectIds.stream()
                .map(projectId -> new GetAllIssueForProjectTask(projectId, gitlabProperty.getOpenIssueUrl(), personProperty.getToken()))
                .map(forkJoinPool::submit)
                .collect(Collectors.toList());

        return pullTaskResults(tasks);
    }

    private static void personMapping(List<Issue> newIssues) {
        final Map<Long, Person> personMap = Stream.concat(
                        newIssues.stream()
                                .map(Issue::getAuthor),
                        newIssues.stream()
                                .flatMap(issue -> issue.getAssignees().stream())
                ).distinct()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Person::getId, p -> p));

        for (Issue newIssue : newIssues) {
            newIssue.setAuthor(personMap.get(newIssue.getAuthor().getId()));

            newIssue.setAssignees(
                    newIssue.getAssignees().stream()
                            .map(reviewer -> personMap.get(reviewer.getId()))
                            .collect(Collectors.toList())
            );
        }
    }

}