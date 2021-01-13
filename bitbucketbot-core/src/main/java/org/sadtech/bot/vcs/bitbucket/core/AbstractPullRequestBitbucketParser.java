package org.sadtech.bot.vcs.bitbucket.core;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.gitlab.context.domain.IdAndStatusPr;
import org.sadtech.bot.gitlab.context.domain.entity.PullRequest;
import org.sadtech.bot.gitlab.context.domain.filter.PullRequestFilter;
import org.sadtech.bot.gitlab.context.service.PullRequestsService;
import org.sadtech.bot.gitlab.core.utils.Pair;
import org.sadtech.bot.gitlab.core.utils.Utils;
import org.sadtech.bot.gitlab.sdk.domain.PullRequestJson;
import org.sadtech.bot.gitlab.sdk.domain.sheet.PullRequestSheetJson;
import org.sadtech.bot.vsc.context.domain.PullRequestStatus;
import org.sadtech.bot.vsc.context.service.PullRequestParser;
import org.springframework.core.convert.ConversionService;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Абстрактный парсер ПРов, для использования в мульти и локал версиях приложения.
 *
 * @author upagge 25.10.2020
 */
@Slf4j
public abstract class AbstractPullRequestBitbucketParser implements PullRequestParser {

    private static final Set<PullRequestStatus> OLD_STATUSES = Stream.of(PullRequestStatus.MERGED, PullRequestStatus.OPEN, PullRequestStatus.DECLINED).collect(Collectors.toSet());

    protected final PullRequestsService pullRequestsService;
    protected final ConversionService conversionService;

    protected AbstractPullRequestBitbucketParser(
            PullRequestsService pullRequestsService,
            ConversionService conversionService
    ) {
        this.pullRequestsService = pullRequestsService;
        this.conversionService = conversionService;
    }

    public void processingOldPullRequests(@NonNull String urlPullRequestOpen, @NonNull String urlPullRequestClose) {
        final Set<Long> existsId = pullRequestsService.getAllId(OLD_STATUSES).stream()
                .map(IdAndStatusPr::getId)
                .collect(Collectors.toSet());
        final Set<Long> openId = getExistsPullRequestIds(urlPullRequestOpen);
        final Set<Long> closeId = getExistsPullRequestIds(urlPullRequestClose);
        final Set<Long> newNotExistsId = existsId.stream()
                .filter(id -> !openId.contains(id) && !closeId.contains(id))
                .collect(Collectors.toSet());
        log.info("Открыты: " + Arrays.toString(openId.toArray()));
        log.info("Закрыты: " + Arrays.toString(closeId.toArray()));
        log.info("Не найдены: " + Arrays.toString(newNotExistsId.toArray()));
        if (!newNotExistsId.isEmpty() && !openId.isEmpty()) {
            pullRequestsService.deleteAllById(newNotExistsId);
        }
    }

    protected abstract Set<Long> getExistsPullRequestIds(@NonNull String bitbucketUrl);

    protected void createNewPullRequest(@NonNull String urlPullRequestOpen, @NonNull String bitbucketToken) {
        Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(urlPullRequestOpen, bitbucketToken, PullRequestSheetJson.class);
        while (sheetJson.isPresent() && sheetJson.get().hasContent()) {
            final PullRequestSheetJson pullRequestBitbucketSheet = sheetJson.get();
            final List<PullRequest> newPullRequest = pullRequestBitbucketSheet.getValues().stream()
                    .collect(Collectors.toMap(pullRequestJson -> new Pair<>(pullRequestJson.getId(), pullRequestJson.getFromRef().getRepository().getId()), pullRequestJson -> pullRequestJson))
                    .values()
                    .stream()
                    .filter(pullRequestJson -> !pullRequestsService.exists(bitbucketIdAndPullRequestId(pullRequestJson)))
                    .map(pullRequestJson -> conversionService.convert(pullRequestJson, PullRequest.class))
                    .collect(Collectors.toList());

            pullRequestsService.createAll(newPullRequest);

            if (pullRequestBitbucketSheet.getNextPageStart() != null) {
                sheetJson = Utils.urlToJson(urlPullRequestOpen + pullRequestBitbucketSheet.getNextPageStart(), bitbucketToken, PullRequestSheetJson.class);
            } else {
                break;
            }
        }
    }

    protected Set<Long> updateOldPullRequests(@NonNull String url, @NonNull String token) {
        Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(url, token, PullRequestSheetJson.class);
        Set<Long> ids = new HashSet<>();
        while (sheetJson.isPresent() && sheetJson.get().hasContent()) {
            final PullRequestSheetJson jsonSheet = sheetJson.get();
            final List<PullRequest> existsPr = getExistsPr(jsonSheet.getValues());

            ids.addAll(
                    pullRequestsService.updateAll(existsPr).stream()
                            .map(PullRequest::getId)
                            .collect(Collectors.toSet())
            );

            if (jsonSheet.getNextPageStart() != null) {
                sheetJson = Utils.urlToJson(url + jsonSheet.getNextPageStart(), token, PullRequestSheetJson.class);
            } else {
                break;
            }
        }
        return ids;
    }

    private List<PullRequest> getExistsPr(@NonNull List<PullRequestJson> pullRequestJsons) {
        return pullRequestJsons.stream()
                .filter(json -> pullRequestsService.exists(bitbucketIdAndPullRequestId(json)))
                .map(json -> conversionService.convert(json, PullRequest.class))
                .collect(Collectors.toList());
    }

    private PullRequestFilter bitbucketIdAndPullRequestId(PullRequestJson json) {
        return PullRequestFilter.builder()
                .bitbucketId(json.getId())
                .bitbucketRepositoryId(json.getFromRef().getRepository().getId())
                .build();
    }

}
