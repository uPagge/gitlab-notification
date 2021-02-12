package org.sadtech.bot.gitlab.core.service.parser;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.entity.Discussion;
import org.sadtech.bot.gitlab.context.domain.entity.MergeRequest;
import org.sadtech.bot.gitlab.context.service.DiscussionService;
import org.sadtech.bot.gitlab.context.service.MergeRequestsService;
import org.sadtech.bot.gitlab.core.config.properties.GitlabProperty;
import org.sadtech.bot.gitlab.core.config.properties.PersonProperty;
import org.sadtech.bot.gitlab.sdk.domain.DiscussionJson;
import org.sadtech.haiti.context.domain.ExistsContainer;
import org.sadtech.haiti.context.exception.ConvertException;
import org.sadtech.haiti.context.page.Sheet;
import org.sadtech.haiti.core.page.PaginationImpl;
import org.sadtech.haiti.utils.network.HttpParse;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.sadtech.haiti.utils.network.HttpParse.ACCEPT;
import static org.sadtech.haiti.utils.network.HttpParse.AUTHORIZATION;
import static org.sadtech.haiti.utils.network.HttpParse.BEARER;

/**
 * // TODO: 11.02.2021 Добавить описание.
 *
 * @author upagge 11.02.2021
 */
@Component
@RequiredArgsConstructor
public class DiscussionParser {

    public static final int COUNT = 100;

    private final DiscussionService discussionService;
    private final MergeRequestsService mergeRequestsService;
    private final ConversionService conversionService;

    private final GitlabProperty gitlabProperty;
    private final PersonProperty personProperty;

    public void scanNewDiscussion() {
        int page = 0;
        Sheet<MergeRequest> mergeRequestSheet = mergeRequestsService.getAll(PaginationImpl.of(page, COUNT));

        while (mergeRequestSheet.hasContent()) {

            final List<MergeRequest> mergeRequests = mergeRequestSheet.getContent();
            for (MergeRequest mergeRequest : mergeRequests) {

                processingMergeRequest(mergeRequest);

            }

            mergeRequestSheet = mergeRequestsService.getAll(PaginationImpl.of(++page, COUNT));
        }
    }

    private void processingMergeRequest(MergeRequest mergeRequest) {
        int page = 1;
        List<DiscussionJson> discussionJson = getDiscussionJson(mergeRequest, page);

        while (!discussionJson.isEmpty()) {

            createNewDiscussion(discussionJson, mergeRequest);

            discussionJson = getDiscussionJson(mergeRequest, ++page);
        }
    }

    private void createNewDiscussion(List<DiscussionJson> discussionJson, MergeRequest mergeRequest) {
        final Set<String> jsonIds = discussionJson.stream()
                .map(DiscussionJson::getId)
                .collect(Collectors.toSet());

        final ExistsContainer<Discussion, String> existsContainer = discussionService.existsById(jsonIds);
        if (!existsContainer.isAllFound()) {
            final List<Discussion> newDiscussions = discussionJson.stream()
                    .filter(json -> existsContainer.getIdNoFound().contains(json.getId()))
                    .map(json -> {
                        final Discussion discussion = conversionService.convert(json, Discussion.class);
                        discussion.setMergeRequest(mergeRequest);
                        discussion.setResponsible(mergeRequest.getAuthor());
                        discussion.getNotes().forEach(
                                note -> {
                                    final String url = MessageFormat.format(gitlabProperty.getUrlNote(), mergeRequest.getWebUrl(), note.getId());
                                    note.setWebUrl(url);
                                }
                        );
                        return discussion;
                    })
                    .collect(Collectors.toList());
            discussionService.createAll(newDiscussions);
        }
    }

    private List<DiscussionJson> getDiscussionJson(MergeRequest mergeRequest, int page) {
        return HttpParse.request(MessageFormat.format(gitlabProperty.getUrlDiscussion(), mergeRequest.getProjectId(), mergeRequest.getTwoId(), page))
                .header(ACCEPT)
                .header(AUTHORIZATION, BEARER + personProperty.getToken())
                .executeList(DiscussionJson.class);
    }

    public void scanOldDiscussions() {
        int page = 0;
        Sheet<Discussion> discussionSheet = discussionService.getAll(PaginationImpl.of(page, 100));

        while (discussionSheet.hasContent()) {
            final List<Discussion> discussions = discussionSheet.getContent();

            for (Discussion discussion : discussions) {
                final Discussion newDiscussion = HttpParse.request(MessageFormat.format(gitlabProperty.getUrlOneDiscussion(), discussion.getMergeRequest().getProjectId(), discussion.getMergeRequest().getTwoId(), discussion.getId()))
                        .header(ACCEPT)
                        .header(AUTHORIZATION, BEARER + personProperty.getToken())
                        .execute(DiscussionJson.class)
                        .map(json -> conversionService.convert(json, Discussion.class))
                        .orElseThrow(() -> new ConvertException("Ошибка парсинга дискуссии"));
                discussionService.update(newDiscussion);
            }

            discussionSheet = discussionService.getAll(PaginationImpl.of(++page, 100));
        }


    }

}
