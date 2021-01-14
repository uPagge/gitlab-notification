package org.sadtech.bot.gitlab.sdk.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * // TODO: 14.01.2021 Добавить описание.
 *
 * @author upagge 14.01.2021
 */
@Data
public class GroupJson {

    private Long id;

    @JsonProperty("web_url")
    private String webUrl;

    private String name;

}
