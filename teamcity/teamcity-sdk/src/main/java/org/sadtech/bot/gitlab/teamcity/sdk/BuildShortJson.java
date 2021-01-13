package org.sadtech.bot.gitlab.teamcity.sdk;

import lombok.Getter;
import lombok.Setter;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Getter
@Setter
public class BuildShortJson {

    private Long id;
    private String projectId;
    private Integer number;
    private BuildState state;
    private BuildStatus status;
    private String branchName;
    private String buildTypeId;
    private String href;
    private String webUrl;

}
