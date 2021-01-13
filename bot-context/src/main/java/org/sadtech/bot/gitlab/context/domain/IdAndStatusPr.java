package org.sadtech.bot.gitlab.context.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.sadtech.bot.vsc.context.domain.PullRequestStatus;

@Setter
@Getter
@AllArgsConstructor
public class IdAndStatusPr {

    private Long id;
    private PullRequestStatus status;

}
