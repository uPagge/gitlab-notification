package dev.struchkov.bot.gitlab.core.service.parser;

import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.bot.gitlab.core.config.properties.PersonProperty;
import dev.struchkov.bot.gitlab.core.service.parser.forktask.GetAllMergeRequestForProjectTask;
import dev.struchkov.bot.gitlab.core.service.parser.forktask.GetSingleMergeRequestTask;
import dev.struchkov.bot.gitlab.core.utils.HttpParse;
import dev.struchkov.bot.gitlab.core.utils.StringUtils;
import dev.struchkov.bot.gitlab.sdk.domain.CommitJson;
import dev.struchkov.bot.gitlab.sdk.domain.MergeRequestJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.struchkov.bot.gitlab.core.utils.HttpParse.ACCEPT;
import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.concurrent.ForkJoinUtils.pullTaskResult;
import static dev.struchkov.haiti.utils.concurrent.ForkJoinUtils.pullTaskResults;

@Slf4j
@Service
public class MergeRequestParser {

    private static final Set<MergeRequestState> OLD_STATUSES = Set.of(
            MergeRequestState.MERGED, MergeRequestState.OPENED, MergeRequestState.CLOSED
    );

    private final GitlabProperty gitlabProperty;
    private final MergeRequestsService mergeRequestsService;
    private final ProjectService projectService;
    private final ConversionService conversionService;
    private final PersonProperty personProperty;

    private final ForkJoinPool forkJoinPool;

    public MergeRequestParser(
            GitlabProperty gitlabProperty,
            MergeRequestsService mergeRequestsService,
            ProjectService projectService,
            ConversionService conversionService,
            PersonProperty personProperty,
            @Qualifier("parserPool") ForkJoinPool forkJoinPool
    ) {
        this.gitlabProperty = gitlabProperty;
        this.mergeRequestsService = mergeRequestsService;
        this.projectService = projectService;
        this.conversionService = conversionService;
        this.personProperty = personProperty;
        this.forkJoinPool = forkJoinPool;
    }

    public void parsingOldMergeRequest() {
        log.debug("Старт обработки старых MR");
        final Set<IdAndStatusPr> existIds = mergeRequestsService.getAllId(OLD_STATUSES);

        final List<MergeRequest> newMergeRequests = getOldMergeRequests(existIds).stream()
                .map(mergeRequestJson -> {
                    final MergeRequest newMergeRequest = conversionService.convert(mergeRequestJson, MergeRequest.class);
                    parsingCommits(newMergeRequest);
                    return newMergeRequest;
                })
                .collect(Collectors.toList());

        if (checkNotEmpty(newMergeRequests)) {
            personMapping(newMergeRequests);
            mergeRequestsService.updateAll(newMergeRequests);
        }
        log.debug("Конец обработки старых MR");
    }

    private List<MergeRequestJson> getOldMergeRequests(Set<IdAndStatusPr> existIds) {
        final List<ForkJoinTask<Optional<MergeRequestJson>>> tasks = existIds.stream()
                .map(
                        existId -> new GetSingleMergeRequestTask(
                                gitlabProperty.getMergeRequestUrl(),
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

    public void parsingNewMergeRequest() {
        log.debug("Старт обработки новых MR");
        final Set<Long> projectIds = projectService.getAllIds();

        final List<MergeRequestJson> mergeRequestJsons = getMergeRequests(projectIds);

        if (checkNotEmpty(mergeRequestJsons)) {
            final Set<Long> jsonIds = mergeRequestJsons.stream()
                    .map(MergeRequestJson::getId)
                    .collect(Collectors.toSet());

            final ExistContainer<MergeRequest, Long> existContainer = mergeRequestsService.existsById(jsonIds);
            log.trace("Из {} полученных MR не найдены в хранилище {}", jsonIds.size(), existContainer.getIdNoFound().size());
            if (!existContainer.isAllFound()) {
                final List<MergeRequest> newMergeRequests = mergeRequestJsons.stream()
                        .filter(json -> existContainer.getIdNoFound().contains(json.getId()))
                        .map(json -> {
                            final MergeRequest mergeRequest = conversionService.convert(json, MergeRequest.class);
                            parsingCommits(mergeRequest);
                            return mergeRequest;
                        })
                        .toList();

                personMapping(newMergeRequests);

                log.trace("Пачка новых MR обработана и отправлена на сохранение. Количество: {} шт.", newMergeRequests.size());
                mergeRequestsService.createAll(newMergeRequests);
            }
        }

        log.debug("Конец обработки новых MR");
    }

    /**
     * Позволяет получить MR для переданных идентификаторов проектов.
     *
     * @param projectIds идентификаторы проектов
     * @return полученные у GitLab MergeRequests
     */
    private List<MergeRequestJson> getMergeRequests(Set<Long> projectIds) {
        final List<ForkJoinTask<List<MergeRequestJson>>> tasks = projectIds.stream()
                .map(projectId -> new GetAllMergeRequestForProjectTask(projectId, gitlabProperty.getOpenMergeRequestsUrl(), personProperty.getToken()))
                .map(forkJoinPool::submit)
                .collect(Collectors.toList());

        return pullTaskResults(tasks);
    }

    private static void personMapping(List<MergeRequest> newMergeRequests) {
        final Map<Long, Person> personMap = Stream.concat(
                        newMergeRequests.stream()
                                .flatMap(mergeRequest -> Stream.of(mergeRequest.getAssignee(), mergeRequest.getAuthor())),
                        newMergeRequests.stream()
                                .flatMap(mergeRequest -> mergeRequest.getReviewers().stream())
                ).distinct()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Person::getId, p -> p));

        for (MergeRequest newMergeRequest : newMergeRequests) {
            newMergeRequest.setAuthor(personMap.get(newMergeRequest.getAuthor().getId()));

            final Person assignee = newMergeRequest.getAssignee();
            if (checkNotNull(assignee)) {
                newMergeRequest.setAssignee(personMap.get(assignee.getId()));
            }

            newMergeRequest.setReviewers(
                    newMergeRequest.getReviewers().stream()
                            .map(reviewer -> personMap.get(reviewer.getId()))
                            .collect(Collectors.toList())
            );
        }
    }

    private void parsingCommits(MergeRequest mergeRequest) {
        final List<CommitJson> commitJson = HttpParse.request(
                        MessageFormat.format(gitlabProperty.getLastCommitOfMergeRequestUrl(), mergeRequest.getProjectId(), mergeRequest.getTwoId())
                )
                .header(ACCEPT)
                .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                .executeList(CommitJson.class);
        if (commitJson != null && !commitJson.isEmpty()) {
            mergeRequest.setDateLastCommit(commitJson.get(0).getCreatedDate());
        }
    }

}
