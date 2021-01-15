package org.sadtech.bot.gitlab.app.service.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.domain.entity.Project;
import org.sadtech.bot.gitlab.context.service.MergeRequestsService;
import org.sadtech.bot.gitlab.context.service.ProjectService;
import org.sadtech.bot.gitlab.core.config.properties.GitlabProperty;
import org.sadtech.bot.gitlab.core.config.properties.PersonProperty;
import org.sadtech.bot.gitlab.sdk.domain.MergeRequestJson;
import org.sadtech.haiti.context.domain.ExistsContainer;
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

    public void parsingOldPullRequest() {
//        processingOldPullRequests(gitlabProperty.getUrlPullRequestOpen(), gitlabProperty.getUrlPullRequestClose());
    }

    public void parsingNewMergeRequest() {

        int page = 0;

        Sheet<Project> projectSheet = projectService.getAll(PaginationImpl.of(page, COUNT));
        while (projectSheet.hasContent()) {
            final List<Project> projects = projectSheet.getContent();

            for (Project project : projects) {
                final List<MergeRequestJson> mergeRequestJsons = HttpParse.request(
                        MessageFormat.format(gitlabProperty.getUrlPullRequestOpen(), project.getId())
                )
                        .header(HttpHeader.of(AUTHORIZATION, BEARER + personProperty.getToken()))
                        .header(ACCEPT)
                        .executeList(MergeRequestJson.class);

                if (!mergeRequestJsons.isEmpty()) {

                    final Set<Long> jsonIds = mergeRequestJsons.stream()
                            .map(MergeRequestJson::getId)
                            .collect(Collectors.toSet());

                    final ExistsContainer<MergeRequest, Long> existsContainer = mergeRequestsService.existsById(jsonIds);
                    if (!existsContainer.isAllFound()) {
                        final List<MergeRequest> newMergeRequests = mergeRequestJsons.stream()
                                .filter(json -> existsContainer.getIdNoFound().contains(json.getId()))
                                .map(json -> conversionService.convert(json, MergeRequest.class))
                                .collect(Collectors.toList());
                        mergeRequestsService.createAll(newMergeRequests);
                    }
                }
            }

            projectSheet = projectService.getAll(PaginationImpl.of(++page, COUNT));
        }


    }

//    private Set<Long> getExistsPullRequestIds(String bitbucketUrl) {
//        Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(url, token, PullRequestSheetJson.class);
//        Set<Long> ids = new HashSet<>();
//        while (sheetJson.isPresent() && sheetJson.get().hasContent()) {
//            final PullRequestSheetJson jsonSheet = sheetJson.get();
//            final List<PullRequest> existsPr = getExistsPr(jsonSheet.getValues());
//
//            ids.addAll(
//                    pullRequestsService.updateAll(existsPr).stream()
//                            .map(PullRequest::getId)
//                            .collect(Collectors.toSet())
//            );
//
//            if (jsonSheet.getNextPageStart() != null) {
//                sheetJson = Utils.urlToJson(url + jsonSheet.getNextPageStart(), token, PullRequestSheetJson.class);
//            } else {
//                break;
//            }
//        }
//        return ids;
//    }
//
//    public void processingOldPullRequests(@NonNull String urlPullRequestOpen, @NonNull String urlPullRequestClose) {
//        final Set<Long> existsId = pullRequestsService.getAllId(OLD_STATUSES).stream()
//                .map(IdAndStatusPr::getId)
//                .collect(Collectors.toSet());
//        final Set<Long> openId = getExistsPullRequestIds(urlPullRequestOpen);
//        final Set<Long> closeId = getExistsPullRequestIds(urlPullRequestClose);
//        final Set<Long> newNotExistsId = existsId.stream()
//                .filter(id -> !openId.contains(id) && !closeId.contains(id))
//                .collect(Collectors.toSet());
//        log.info("Открыты: " + Arrays.toString(openId.toArray()));
//        log.info("Закрыты: " + Arrays.toString(closeId.toArray()));
//        log.info("Не найдены: " + Arrays.toString(newNotExistsId.toArray()));
//        if (!newNotExistsId.isEmpty() && !openId.isEmpty()) {
//            pullRequestsService.deleteAllById(newNotExistsId);
//        }
//    }
//
//    private List<PullRequest> getExistsPr(@NonNull List<PullRequestJson> pullRequestJsons) {
//        return pullRequestJsons.stream()
//                .filter(json -> pullRequestsService.exists(bitbucketIdAndPullRequestId(json)))
//                .map(json -> conversionService.convert(json, PullRequest.class))
//                .collect(Collectors.toList());
//    }
//

}
