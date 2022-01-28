package dev.struchkov.bot.gitlab.core.service.parser;

import dev.struchkov.bot.gitlab.context.domain.IdAndStatusPr;
import dev.struchkov.bot.gitlab.context.domain.MergeRequestState;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.entity.Project;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.bot.gitlab.context.service.ProjectService;
import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.bot.gitlab.core.config.properties.PersonProperty;
import dev.struchkov.bot.gitlab.core.utils.StringUtils;
import dev.struchkov.bot.gitlab.sdk.domain.CommitJson;
import dev.struchkov.bot.gitlab.sdk.domain.MergeRequestJson;
import dev.struchkov.haiti.context.domain.ExistsContainer;
import dev.struchkov.haiti.context.exception.NotFoundException;
import dev.struchkov.haiti.context.page.Sheet;
import dev.struchkov.haiti.context.page.impl.PaginationImpl;
import dev.struchkov.haiti.utils.network.HttpParse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        final Set<IdAndStatusPr> existIds = mergeRequestsService.getAllId(OLD_STATUSES);

        for (IdAndStatusPr existId : existIds) {
            final MergeRequest mergeRequest = HttpParse.request(MessageFormat.format(gitlabProperty.getUrlPullRequest(), existId.getProjectId(), existId.getTwoId()))
                    .header(ACCEPT)
                    .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                    .execute(MergeRequestJson.class)
                    .map(json -> {
                        final MergeRequest newMergeRequest = conversionService.convert(json, MergeRequest.class);
                        parsingCommits(newMergeRequest);
                        return newMergeRequest;
                    })
                    .orElseThrow(NotFoundException.supplier("МержРеквест не найден, возможно удален"));
            mergeRequestsService.update(mergeRequest);
        }

    }

    public void parsingNewMergeRequest() {
        int page = 0;
        Sheet<Project> projectSheet = projectService.getAll(PaginationImpl.of(page, COUNT));

        while (projectSheet.hasContent()) {
            final List<Project> projects = projectSheet.getContent();

            for (Project project : projects) {
                projectProcessing(project);
            }

            projectSheet = projectService.getAll(PaginationImpl.of(++page, COUNT));
        }
    }

    private void projectProcessing(Project project) {
        int page = 1;
        List<MergeRequestJson> mergeRequestJsons = getMergeRequestJsons(project, page);

        while (!mergeRequestJsons.isEmpty()) {

            final Set<Long> jsonIds = mergeRequestJsons.stream()
                    .map(MergeRequestJson::getId)
                    .collect(Collectors.toSet());

            final ExistsContainer<MergeRequest, Long> existsContainer = mergeRequestsService.existsById(jsonIds);
            if (!existsContainer.isAllFound()) {
                final List<MergeRequest> newMergeRequests = mergeRequestJsons.stream()
                        .filter(json -> existsContainer.getIdNoFound().contains(json.getId()))
                        .map(json -> {
                            final MergeRequest mergeRequest = conversionService.convert(json, MergeRequest.class);
                            parsingCommits(mergeRequest);
                            return mergeRequest;
                        })
                        .toList();

                mergeRequestsService.createAll(newMergeRequests);
            }

            mergeRequestJsons = getMergeRequestJsons(project, page++);
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

    private List<MergeRequestJson> getMergeRequestJsons(Project project, int page) {
        return HttpParse.request(MessageFormat.format(gitlabProperty.getUrlPullRequestOpen(), project.getId(), page))
                .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                .header(ACCEPT)
                .executeList(MergeRequestJson.class);
    }

}
