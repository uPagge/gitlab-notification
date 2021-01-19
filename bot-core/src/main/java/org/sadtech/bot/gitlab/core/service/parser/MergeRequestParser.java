package org.sadtech.bot.gitlab.core.service.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.entity.Project;
import org.sadtech.bot.gitlab.context.service.MergeRequestsService;
import org.sadtech.bot.gitlab.context.service.ProjectService;
import org.sadtech.bot.gitlab.core.config.properties.GitlabProperty;
import org.sadtech.bot.gitlab.core.config.properties.PersonProperty;
import org.sadtech.bot.gitlab.sdk.domain.CommitJson;
import org.sadtech.bot.gitlab.sdk.domain.MergeRequestJson;
import org.sadtech.haiti.context.domain.ExistsContainer;
import org.sadtech.haiti.context.exception.NotFoundException;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.core.page.PaginationImpl;
import org.sadtech.haiti.utils.network.HttpHeader;
import org.sadtech.haiti.utils.network.HttpParse;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.sadtech.haiti.utils.network.HttpParse.ACCEPT;
import static org.sadtech.haiti.utils.network.HttpParse.AUTHORIZATION;
import static org.sadtech.haiti.utils.network.HttpParse.BEARER;

@Slf4j
@Service
@RequiredArgsConstructor
public class MergeRequestParser {


    public static final Integer COUNT = 100;
    private static final Set<MergeRequestState> OLD_STATUSES = Stream.of(
            MergeRequestState.MERGED, MergeRequestState.OPENED, MergeRequestState.CLOSED
    ).collect(Collectors.toSet());

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
                    .header(AUTHORIZATION, BEARER + personProperty.getToken())
                    .execute(MergeRequestJson.class)
                    .map(json -> {
                        final MergeRequest newMergeRequest = conversionService.convert(json, MergeRequest.class);
                        parsingCommits(newMergeRequest);
                        return newMergeRequest;
                    })
                    .orElseThrow(() -> new NotFoundException("МержРеквест не найден, возможно удален"));
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
                        .collect(Collectors.toList());

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
                .header(AUTHORIZATION, BEARER + personProperty.getToken())
                .executeList(CommitJson.class);
        mergeRequest.setDateLastCommit(commitJson.get(0).getCreatedDate());
    }

    private List<MergeRequestJson> getMergeRequestJsons(Project project, int page) {
        return HttpParse.request(MessageFormat.format(gitlabProperty.getUrlPullRequestOpen(), project.getId(), page))
                .header(HttpHeader.of(AUTHORIZATION, BEARER + personProperty.getToken()))
                .header(ACCEPT)
                .executeList(MergeRequestJson.class);
    }

}
