package dev.struchkov.bot.gitlab.core.service.parser;

import dev.struchkov.bot.gitlab.context.domain.ExistContainer;
import dev.struchkov.bot.gitlab.context.domain.entity.Discussion;
import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequestForDiscussion;
import dev.struchkov.bot.gitlab.context.domain.entity.Note;
import dev.struchkov.bot.gitlab.context.domain.entity.Person;
import dev.struchkov.bot.gitlab.context.service.DiscussionService;
import dev.struchkov.bot.gitlab.context.service.MergeRequestsService;
import dev.struchkov.bot.gitlab.core.config.properties.GitlabProperty;
import dev.struchkov.bot.gitlab.core.config.properties.PersonProperty;
import dev.struchkov.bot.gitlab.core.utils.HttpParse;
import dev.struchkov.bot.gitlab.sdk.domain.DiscussionJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.struchkov.bot.gitlab.core.utils.HttpParse.ACCEPT;
import static dev.struchkov.bot.gitlab.core.utils.StringUtils.H_PRIVATE_TOKEN;
import static dev.struchkov.haiti.utils.Checker.checkFalse;
import static dev.struchkov.haiti.utils.Checker.checkNotEmpty;
import static dev.struchkov.haiti.utils.Checker.checkNotNull;

/**
 * Парсер обсуждений.
 *
 * @author upagge 11.02.2021
 */
@Slf4j
@Component
public class DiscussionParser {

    public static final int PAGE_COUNT = 100;

    private final DiscussionService discussionService;

    private final MergeRequestsService mergeRequestsService;
    private final ConversionService conversionService;
    private final ForkJoinPool forkJoinPool;

    private final GitlabProperty gitlabProperty;

    private final PersonProperty personProperty;

    public DiscussionParser(
            DiscussionService discussionService,
            MergeRequestsService mergeRequestsService,
            ConversionService conversionService,
            @Qualifier("parserPool") ForkJoinPool forkJoinPool,
            GitlabProperty gitlabProperty,
            PersonProperty personProperty
    ) {
        this.discussionService = discussionService;
        this.mergeRequestsService = mergeRequestsService;
        this.conversionService = conversionService;
        this.forkJoinPool = forkJoinPool;
        this.gitlabProperty = gitlabProperty;
        this.personProperty = personProperty;
    }

    /**
     * Поиск новых обсуждений
     */
    public void scanNewDiscussion() {
        log.debug("Старт обработки новых дискуссий");
        final List<MergeRequestForDiscussion> mergeRequests = mergeRequestsService.getAllForDiscussion();

        mergeRequests.forEach(this::processingNewDiscussion);

        log.debug("Конец обработки новых дискуссий");
    }

    private void processingNewDiscussion(MergeRequestForDiscussion mergeRequest) {
        int page = 1;
        final List<DiscussionJson> discussionJson = getDiscussionJson(mergeRequest, page);

        if (checkNotEmpty(discussionJson)) {
            while (discussionJson.size() == PAGE_COUNT) {
                discussionJson.addAll(getDiscussionJson(mergeRequest, ++page));
            }
            createNewDiscussion(discussionJson, mergeRequest);
        }
    }

    private void createNewDiscussion(List<DiscussionJson> discussionJson, MergeRequestForDiscussion mergeRequest) {
        final Set<String> discussionIds = discussionJson.stream()
                .map(DiscussionJson::getId)
                .collect(Collectors.toUnmodifiableSet());

        final ExistContainer<Discussion, String> existContainer = discussionService.existsById(discussionIds);
        final Set<String> notFoundIds = existContainer.getIdNoFound();
        if (checkFalse(existContainer.isAllFound())) {
            final List<Discussion> newDiscussions = discussionJson.stream()
                    .filter(json -> notFoundIds.contains(json.getId()))
                    .map(json -> {
                        final Discussion discussion = conversionService.convert(json, Discussion.class);
                        discussion.setMergeRequest(mergeRequest);
                        discussion.setResponsible(mergeRequest.getAuthor());
                        discussion.getNotes().forEach(createNoteLink(mergeRequest));
                        return discussion;
                    })
                    // Фильтрация специально стоит после map(). Таким образом отбрасываются системные уведомления
                    .filter(discussion -> checkNotEmpty(discussion.getNotes()))
                    .toList();

            if (checkNotEmpty(newDiscussions)) {
                personMapping(newDiscussions);
                discussionService.createAll(newDiscussions);
            }

        }
    }

    private void personMapping(List<Discussion> newDiscussions) {
        final Stream<Person> firstStream = Stream.concat(
                newDiscussions.stream()
                        .flatMap(discussion -> discussion.getNotes().stream())
                        .map(Note::getResolvedBy)
                        .filter(Objects::nonNull),
                newDiscussions.stream()
                        .flatMap(discussion -> discussion.getNotes().stream())
                        .map(Note::getAuthor)
                        .filter(Objects::nonNull)
        );

        final Map<Long, Person> personMap = Stream.concat(
                        firstStream,
                        newDiscussions.stream()
                                .map(Discussion::getResponsible)
                                .filter(Objects::nonNull)
                ).distinct()
                .collect(Collectors.toMap(Person::getId, p -> p));

        for (Discussion newDiscussion : newDiscussions) {
            final Person responsible = newDiscussion.getResponsible();
            if (checkNotNull(responsible)) {
                newDiscussion.setResponsible(personMap.get(responsible.getId()));
            }

            for (Note note : newDiscussion.getNotes()) {
                note.setAuthor(personMap.get(note.getAuthor().getId()));

                final Person resolvedBy = note.getResolvedBy();
                if (checkNotNull(resolvedBy)) {
                    note.setResolvedBy(personMap.get(resolvedBy.getId()));
                }
            }
        }
    }

    /**
     * Сканирование старых обсуждений на предмет новых комментарие
     */
    public void scanOldDiscussions() {
        log.debug("Старт обработки старых дискуссий");
        final List<Discussion> discussions = discussionService.getAll();

        final List<Discussion> newDiscussions = new ArrayList<>();
        for (Discussion discussion : discussions) {
            if (checkNotNull(discussion.getMergeRequest())) {
                getOldDiscussionJson(discussion)
                        .map(json -> {
                            final Discussion newDiscussion = conversionService.convert(json, Discussion.class);
                            newDiscussion.getNotes().forEach(createNoteLink(discussion.getMergeRequest()));
                            return newDiscussion;
                        }).ifPresent(newDiscussions::add);
            } else {
                discussionService.deleteById(discussion.getId());
            }
        }

        if (checkNotEmpty(newDiscussions)) {
            personMapping(newDiscussions);
            discussionService.updateAll(newDiscussions);
        }

        log.debug("Конец обработки старых дискуссий");
    }

    private Optional<DiscussionJson> getOldDiscussionJson(Discussion discussion) {
        return HttpParse.request(createLinkOldDiscussion(discussion))
                .header(ACCEPT)
                .header(H_PRIVATE_TOKEN, personProperty.getToken())
                .execute(DiscussionJson.class);
    }

    private String createLinkOldDiscussion(Discussion discussion) {
        return MessageFormat.format(
                gitlabProperty.getDiscussionUrl(),
                discussion.getMergeRequest().getProjectId(),
                discussion.getMergeRequest().getTwoId(),
                discussion.getId()
        );
    }

    private List<DiscussionJson> getDiscussionJson(MergeRequestForDiscussion mergeRequest, int page) {
        return HttpParse.request(MessageFormat.format(gitlabProperty.getDiscussionsUrl(), mergeRequest.getProjectId(), mergeRequest.getTwoId(), page, PAGE_COUNT))
                .header(ACCEPT)
                .header(H_PRIVATE_TOKEN, personProperty.getToken())
                .executeList(DiscussionJson.class);
    }

    private Consumer<Note> createNoteLink(MergeRequestForDiscussion mergeRequest) {
        return note -> {
            final String url = MessageFormat.format(
                    gitlabProperty.getNoteUrl(),
                    mergeRequest.getWebUrl(),
                    note.getId()
            );
            note.setWebUrl(url);
        };
    }

}
