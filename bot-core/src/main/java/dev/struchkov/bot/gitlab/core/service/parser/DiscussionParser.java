package dev.struchkov.bot.gitlab.core.service.parser;

import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.service.DiscussionService;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.bot.gitlab.core.config.properties.PersonProperty;
import dev.struchkov.bot.gitlab.core.utils.StringUtils;
import dev.struchkov.bot.gitlab.sdk.domain.DiscussionJson;
import dev.struchkov.haiti.context.domain.ExistsContainer;
import dev.struchkov.haiti.context.page.Sheet;
import dev.struchkov.haiti.context.page.impl.PaginationImpl;
import dev.struchkov.haiti.utils.network.HttpParse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.struchkov.haiti.utils.network.HttpParse.ACCEPT;

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
                    .filter(discussion -> discussion.getNotes() != null && !discussion.getNotes().isEmpty())
                    .collect(Collectors.toList());
            discussionService.createAll(newDiscussions);
        }
    }

    private List<DiscussionJson> getDiscussionJson(MergeRequest mergeRequest, int page) {
        return HttpParse.request(MessageFormat.format(gitlabProperty.getUrlDiscussion(), mergeRequest.getProjectId(), mergeRequest.getTwoId(), page))
                .header(ACCEPT)
                .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                .executeList(DiscussionJson.class);
    }

    public void scanOldDiscussions() {
        int page = 0;
        Sheet<Discussion> discussionSheet = discussionService.getAll(PaginationImpl.of(page, 100));

        while (discussionSheet.hasContent()) {
            final List<Discussion> discussions = discussionSheet.getContent();

            for (Discussion discussion : discussions) {
                if (discussion.getMergeRequest() != null) {
                    final Optional<Discussion> optNewDiscussion = HttpParse.request(MessageFormat.format(gitlabProperty.getUrlOneDiscussion(), discussion.getMergeRequest().getProjectId(), discussion.getMergeRequest().getTwoId(), discussion.getId()))
                            .header(ACCEPT)
                            .header(StringUtils.H_PRIVATE_TOKEN, personProperty.getToken())
                            .execute(DiscussionJson.class)
                            .map(json -> {
                                final Discussion newDiscussion = conversionService.convert(json, Discussion.class);
                                newDiscussion.getNotes().forEach(
                                        note -> {
                                            final String url = MessageFormat.format(gitlabProperty.getUrlNote(), discussion.getMergeRequest().getWebUrl(), note.getId());
                                            note.setWebUrl(url);
                                        }
                                );
                                return newDiscussion;
                            });
                    optNewDiscussion.ifPresent(discussionService::update);
                } else {
                    discussionService.deleteById(discussion.getId());
                }
            }

            discussionSheet = discussionService.getAll(PaginationImpl.of(++page, 100));
        }


    }

}
