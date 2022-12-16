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
import dev.struchkov.bot.gitlab.core.utils.StringUtils;
import dev.struchkov.bot.gitlab.sdk.domain.CommitJson;
import dev.struchkov.bot.gitlab.sdk.domain.MergeRequestJson;
import dev.struchkov.haiti.utils.network.HttpParse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;
import static dev.struchkov.haiti.utils.network.HttpParse.ACCEPT;

@Slf4j
@Service
@RequiredArgsConstructor
public class MergeRequestParser {

    public static final Integer COUNT = 100;
    private static final Set<MergeRequestState> OLD_STATUSES = Set.of(
            MergeRequestState.MERGED, MergeRequestState.OPENED, MergeRequestState.CLOSED
    );

    private final GitlabProperty gitlabProperty;
    private final MergeRequestsService mergeRequestsService;
    private final ProjectService projectService;
    private final ConversionService conversionService;
    private final PersonProperty personProperty;

    public void parsingOldMergeRequest() {
        log.debug("Старт обработки старых MR");
        final Set<IdAndStatusPr> existIds = mergeRequestsService.getAllId(OLD_STATUSES);

        final List<MergeRequest> mergeRequests = existIds.stream()
                .map(this::getMergeRequest)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(mergeRequestJson -> {
                    final MergeRequest newMergeRequest = conversionService.convert(mergeRequestJson, MergeRequest.class);
                    parsingCommits(newMergeRequest);
                    return newMergeRequest;
                })
                .collect(Collectors.toList());

        if (checkNotEmpty(mergeRequests)) {
            personMapping(mergeRequests);
            mergeRequestsService.updateAll(mergeRequests);
        }
        log.debug("Конец обработки старых MR");
    }

    public void parsingNewMergeRequest() {
        log.debug("Старт обработки новых MR");
        final Set<Long> projectIds = projectService.getAllIds();

        for (Long projectId : projectIds) {
            projectProcessing(projectId);
        }

        log.debug("Конец обработки новых MR");
    }

    private void projectProcessing(Long projectId) {
        int page = 1;
        List<MergeRequestJson> mergeRequestJsons = getMergeRequestJsons(projectId, page);

        while (checkNotEmpty(mergeRequestJsons)) {

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

            mergeRequestJsons = getMergeRequestJsons(projectId, page++);
        }
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
                        MessageFormat.format(gitlabProperty.getUrlCommit(), mergeRequest.getProjectId(), mergeRequest.getTwoId())
                )
                .header(ACCEPT)
                .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                .executeList(CommitJson.class);
        if (commitJson != null && !commitJson.isEmpty()) {
            mergeRequest.setDateLastCommit(commitJson.get(0).getCreatedDate());
        }
    }

    private List<MergeRequestJson> getMergeRequestJsons(Long projectId, int page) {
        final List<MergeRequestJson> jsons = HttpParse.request(MessageFormat.format(gitlabProperty.getUrlPullRequestOpen(), projectId, page))
                .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                .header(ACCEPT)
                .executeList(MergeRequestJson.class);
        log.trace("Получено {} шт потенциально новых MR для проекта id:'{}' ", jsons.size(), projectId);
        return jsons;
    }

    private Optional<MergeRequestJson> getMergeRequest(IdAndStatusPr existId) {
        final String mrUrl = MessageFormat.format(gitlabProperty.getUrlPullRequest(), existId.getProjectId(), existId.getTwoId());
        return HttpParse.request(mrUrl)
                .header(ACCEPT)
                .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                .execute(MergeRequestJson.class);
    }

}
