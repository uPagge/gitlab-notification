package dev.struchkov.bot.gitlab.context.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @author upagge 15.01.2021
 */
@Getter
@Setter
public class PersonInformation {

    private String username;
    private String name;
    private Long id;
    private String telegramId;

}
