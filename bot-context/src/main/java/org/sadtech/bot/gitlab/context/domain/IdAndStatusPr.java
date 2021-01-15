package org.sadtech.bot.gitlab.context.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class IdAndStatusPr {

    private Long id;
    private Long twoId;
    private Long projectId;
    private MergeRequestState status;

}
