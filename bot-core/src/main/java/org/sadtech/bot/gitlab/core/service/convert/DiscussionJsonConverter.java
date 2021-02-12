package org.sadtech.bot.gitlab.core.service.convert;

import lombok.RequiredArgsConstructor;
import org.sadtech.bot.gitlab.context.domain.entity.Discussion;
import org.sadtech.bot.gitlab.sdk.domain.DiscussionJson;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * // TODO: 11.02.2021 Добавить описание.
 *
 * @author upagge 11.02.2021
 */
@Component
@RequiredArgsConstructor
public class DiscussionJsonConverter implements Converter<DiscussionJson, Discussion> {

    private final NoteJsonConvert noteJsonConvert;

    @Override
    public Discussion convert(DiscussionJson source) {
        final Discussion discussion = new Discussion();
        discussion.setId(source.getId());
        discussion.setNotes(
                source.getNotes().stream()
                        .map(noteJsonConvert::convert)
                        .collect(Collectors.toList())
        );
        return discussion;
    }
}
