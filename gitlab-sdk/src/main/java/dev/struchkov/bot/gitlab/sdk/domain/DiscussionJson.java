package dev.struchkov.bot.gitlab.sdk.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * // TODO: 11.02.2021 Добавить описание.
 *
 * @author upagge 11.02.2021
 */
@Getter
@Setter
public class DiscussionJson {

    private String id;
    private List<NoteJson> notes;

}
