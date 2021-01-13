package org.sadtech.bot.gitlab.sdk.domain;

import lombok.Data;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [04.02.2020]
 */
@Data
public class RepositoryJson {

    private Long id;
    private String slug;
    private ProjectJson project;

}
