package org.sadtech.bot.gitlab.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [31.01.2020]
 */
@Data
public class AuthorJson {

    private Long id;
    private String name;
    private String userName;

    @JsonProperty("web_url")
    private String webUrl;

}
