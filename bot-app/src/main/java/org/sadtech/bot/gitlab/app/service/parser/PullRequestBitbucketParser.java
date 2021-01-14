package org.sadtech.bot.gitlab.app.service.parser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.gitlab.context.domain.entity.PullRequest;
import org.sadtech.bot.gitlab.context.service.PullRequestsService;
import org.sadtech.bot.gitlab.core.config.properties.GitlabProperty;
import org.sadtech.bot.gitlab.core.utils.Pair;
import org.sadtech.bot.gitlab.sdk.domain.PullRequestJson;
import org.sadtech.bot.vsc.context.domain.PullRequestStatus;
import org.sadtech.bot.vsc.context.service.PullRequestParser;
import org.sadtech.haiti.utils.network.HttpHeader;
import org.sadtech.haiti.utils.network.HttpParse;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

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
public class PullRequestBitbucketParser implements PullRequestParser {

    private static final Set<PullRequestStatus> OLD_STATUSES = Stream.of(PullRequestStatus.MERGED, PullRequestStatus.OPEN, PullRequestStatus.DECLINED).collect(Collectors.toSet());

    private final GitlabProperty gitlabProperty;
    private final PullRequestsService pullRequestsService;
    private final ConversionService conversionService;

    @Override
    public void parsingOldPullRequest() {
//        processingOldPullRequests(gitlabProperty.getUrlPullRequestOpen(), gitlabProperty.getUrlPullRequestClose());
    }

    @Override
    public void parsingNewPullRequest() {

        final List<PullRequestJson> pullRequestJsons = HttpParse.request(gitlabProperty.getUrlPullRequestOpen())
                .header(HttpHeader.of(AUTHORIZATION, BEARER + gitlabProperty.getToken()))
                .header(ACCEPT)
                .executeList(PullRequestJson.class);

        while (pullRequestJsons != null && !pullRequestJsons.isEmpty()) {
            final List<PullRequest> newPullRequest = pullRequestJsons.stream()
                    .collect(Collectors.toMap(pullRequestJson -> new Pair<>(pullRequestJson.getId(), pullRequestJson.getFromRef().getRepository().getId()), pullRequestJson -> pullRequestJson))
                    .values()
                    .stream()
//                    .filter(pullRequestJson -> !pullRequestsService.exists(bitbucketIdAndPullRequestId(pullRequestJson)))
                    .map(pullRequestJson -> conversionService.convert(pullRequestJson, PullRequest.class))
                    .collect(Collectors.toList());

            pullRequestsService.createAll(newPullRequest);
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
//    private PullRequestFilter bitbucketIdAndPullRequestId(PullRequestJson json) {
//        return PullRequestFilter.builder()
//                .bitbucketId(json.getId())
//                .bitbucketRepositoryId(json.getFromRef().getRepository().getId())
//                .build();
//    }

}
