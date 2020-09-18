package org.sadtech.bot.vcs.bitbucket.app.service.parser;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sadtech.bot.vcs.bitbucket.sdk.domain.PullRequestJson;
import org.sadtech.bot.vcs.bitbucket.sdk.domain.sheet.PullRequestSheetJson;
import org.sadtech.bot.vcs.core.config.properties.BitbucketProperty;
import org.sadtech.bot.vcs.core.domain.IdAndStatusPr;
import org.sadtech.bot.vcs.core.domain.PullRequestStatus;
import org.sadtech.bot.vcs.core.domain.entity.Person;
import org.sadtech.bot.vcs.core.domain.entity.PullRequest;
import org.sadtech.bot.vcs.core.domain.filter.PullRequestFilter;
import org.sadtech.bot.vcs.core.service.PersonService;
import org.sadtech.bot.vcs.core.service.PullRequestsService;
import org.sadtech.bot.vcs.core.service.Utils;
import org.sadtech.bot.vcs.core.service.parser.PullRequestParser;
import org.sadtech.bot.vcs.core.utils.Pair;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PullRequestBitbucketParser implements PullRequestParser {

    private static final Set<PullRequestStatus> OLD_STATUSES = Stream.of(PullRequestStatus.MERGED, PullRequestStatus.OPEN, PullRequestStatus.DECLINED).collect(Collectors.toSet());

    private final PullRequestsService pullRequestsService;
    private final PersonService personService;
    private final ConversionService conversionService;
    private final BitbucketProperty bitbucketProperty;

    @Override
    public void parsingOldPullRequest() {
        final Set<Long> existsId = pullRequestsService.getAllId(OLD_STATUSES).stream()
                .map(IdAndStatusPr::getId)
                .collect(Collectors.toSet());
        final Set<Long> openId = parsingPullRequest(bitbucketProperty.getUrlPullRequestOpen());
        final Set<Long> closeId = parsingPullRequest(bitbucketProperty.getUrlPullRequestClose());
        final Set<Long> newNotExistsId = existsId.stream()
                .filter(id -> !openId.contains(id) && !closeId.contains(id))
                .collect(Collectors.toSet());
        log.info("Открыты: " + Arrays.toString(openId.toArray()));
        log.info("Закрыты: " + Arrays.toString(closeId.toArray()));
        log.info("Не найдены: " + Arrays.toString(newNotExistsId.toArray()));
        if (!newNotExistsId.isEmpty()) {
            pullRequestsService.deleteAllById(newNotExistsId);
        }
    }

    @Override
    public void parsingNewPullRequest() {
        final List<Person> users = personService.getAllRegister();
        for (Person user : users) {
            Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(bitbucketProperty.getUrlPullRequestOpen(), user.getToken(), PullRequestSheetJson.class);
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
                    sheetJson = Utils.urlToJson(bitbucketProperty.getUrlPullRequestOpen() + pullRequestBitbucketSheet.getNextPageStart(), bitbucketProperty.getToken(), PullRequestSheetJson.class);
                } else {
                    break;
                }
            }
        }
    }

    private Set<Long> parsingPullRequest(@NonNull String url) {
        final List<Person> users = personService.getAllRegister();
        final Set<Long> ids = new HashSet<>();
        for (Person user : users) {
            Optional<PullRequestSheetJson> sheetJson = Utils.urlToJson(url, user.getToken(), PullRequestSheetJson.class);
            while (sheetJson.isPresent() && sheetJson.get().hasContent()) {
                final PullRequestSheetJson jsonSheet = sheetJson.get();
                final List<PullRequest> existsPr = getExistsPr(jsonSheet.getValues());

                ids.addAll(
                        pullRequestsService.updateAll(existsPr).stream()
                                .map(PullRequest::getId)
                                .collect(Collectors.toSet())
                );

                if (jsonSheet.getNextPageStart() != null) {
                    sheetJson = Utils.urlToJson(url + jsonSheet.getNextPageStart(), bitbucketProperty.getToken(), PullRequestSheetJson.class);
                } else {
                    break;
                }
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
