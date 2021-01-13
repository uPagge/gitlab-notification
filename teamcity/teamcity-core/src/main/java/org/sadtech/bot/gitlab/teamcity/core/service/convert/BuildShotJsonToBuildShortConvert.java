package org.sadtech.bot.gitlab.teamcity.core.service.convert;

import org.sadtech.bot.gitlab.teamcity.core.domain.entity.BuildShort;
import org.sadtech.bot.gitlab.teamcity.sdk.BuildShortJson;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * // TODO: 21.09.2020 Добавить описание.
 *
 * @author upagge 21.09.2020
 */
@Component
public class BuildShotJsonToBuildShortConvert implements Converter<BuildShortJson, BuildShort> {

    @Override
    public BuildShort convert(BuildShortJson source) {
        final BuildShort buildShort = new BuildShort();
        buildShort.setApiUrl(source.getHref());
        buildShort.setBranchName(source.getBranchName());
        buildShort.setId(source.getId());
        buildShort.setNumber(source.getNumber());
        buildShort.setState(source.getState());
        buildShort.setStatus(source.getStatus());
        buildShort.setUrl(source.getWebUrl());
        buildShort.setProjectId(source.getProjectId());
        buildShort.setBuildTypeId(source.getBuildTypeId());
        return buildShort;
    }

}
