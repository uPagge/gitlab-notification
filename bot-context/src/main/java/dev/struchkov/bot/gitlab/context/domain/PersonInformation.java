package dev.struchkov.bot.gitlab.context.domain;

import lombok.Data;

/**
 * // TODO: 15.01.2021 Добавить описание.
 *
 * @author upagge 15.01.2021
 */
@Data
public class PersonInformation {

    private String username;
    private String name;
    private Long id;
    private Long telegramId;

}
