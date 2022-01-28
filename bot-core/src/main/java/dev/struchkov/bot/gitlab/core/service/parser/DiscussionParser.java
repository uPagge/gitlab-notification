package dev.struchkov.bot.gitlab.core.service.parser;

import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequest;
import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import dev.struchkov.bot.gitlab.context.service.DiscussionService;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.bot.gitlab.core.config.properties.PersonProperty;
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
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static dev.struchkov.bot.gitlab.core.utils.StringUtils.H_PRIVATE_TOKEN;
import static dev.struchkov.haiti.utils.network.HttpParse.ACCEPT;

/**
 * Парсер обсуждений.
 *
 * @author upagge 11.02.2021
 */
@Component
@RequiredArgsConstructor
public class DiscussionParser {

    public static final int COUNT = 500;

    private final DiscussionService discussionService;
    private final MergeRequestsService mergeRequestsService;
    private final ConversionService conversionService;

    private final GitlabProperty gitlabProperty;
    private final PersonProperty personProperty;

    /**
     * Поиск новых обсуждений
     */
    public void scanNewDiscussion() {
        int page = 0;
        Sheet<MergeRequest> mergeRequestSheet = mergeRequestsService.getAll(PaginationImpl.of(page, COUNT));

        while (mergeRequestSheet.hasContent()) {
            mergeRequestSheet.getContent()
                    .forEach(this::processingMergeRequest);
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
        final Set<String> discussionIds = discussionJson.stream()
                .map(DiscussionJson::getId)
                .collect(Collectors.toUnmodifiableSet());

        final ExistsContainer<Discussion, String> existsContainer = discussionService.existsById(discussionIds);
        if (!existsContainer.isAllFound()) {
            final List<Discussion> newDiscussions = discussionJson.stream()
                    .filter(json -> existsContainer.getIdNoFound().contains(json.getId()))
                    .map(json -> {
                        final Discussion discussion = conversionService.convert(json, Discussion.class);
                        discussion.setMergeRequest(mergeRequest);
                        discussion.setResponsible(mergeRequest.getAuthor());
                        discussion.getNotes().forEach(createNoteLink(mergeRequest));
                        return discussion;
                    })
                    .filter(discussion -> discussion.getNotes() != null && !discussion.getNotes().isEmpty())
                    .toList();
            discussionService.createAll(newDiscussions);
        }
    }

    /**
     * Сканирование старых обсуждений на предмет новых комментарие
     */
    public void scanOldDiscussions() {
        int page = 0;
        Sheet<Discussion> discussionSheet = discussionService.getAll(PaginationImpl.of(page, COUNT));

        while (discussionSheet.hasContent()) {
            final List<Discussion> discussions = discussionSheet.getContent();

            for (Discussion discussion : discussions) {
                if (discussion.getMergeRequest() != null) {
                    final Optional<Discussion> optNewDiscussion = HttpParse.request(createLinkOldDiscussion(discussion))
                            .header(ACCEPT)
                            .header(H_PRIVATE_TOKEN, personProperty.getToken())
                            .execute(DiscussionJson.class)
                            .map(json -> {
                                final Discussion newDiscussion = conversionService.convert(json, Discussion.class);
                                newDiscussion.getNotes().forEach(createNoteLink(discussion.getMergeRequest()));
                                return newDiscussion;
                            });
                    optNewDiscussion.ifPresent(discussionService::update);
                } else {
                    discussionService.deleteById(discussion.getId());
                }
            }

            discussionSheet = discussionService.getAll(PaginationImpl.of(++page, COUNT));
        }

    }

    private String createLinkOldDiscussion(Discussion discussion) {
        return MessageFormat.format(
                gitlabProperty.getUrlOneDiscussion(),
                discussion.getMergeRequest().getProjectId(),
                discussion.getMergeRequest().getTwoId(),
                discussion.getId()
        );
    }

    private List<DiscussionJson> getDiscussionJson(MergeRequest mergeRequest, int page) {
        return HttpParse.request(MessageFormat.format(gitlabProperty.getUrlDiscussion(), mergeRequest.getProjectId(), mergeRequest.getTwoId(), page))
                .header(ACCEPT)
                .header(H_PRIVATE_TOKEN, personProperty.getToken())
                .executeList(DiscussionJson.class);
    }

    private Consumer<Note> createNoteLink(MergeRequest mergeRequest) {
        return note -> {
            final String url = MessageFormat.format(gitlabProperty.getUrlNote(), mergeRequest.getWebUrl(), note.getId());
            note.setWebUrl(url);
        };
    }

}
