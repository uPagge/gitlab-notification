package dev.struchkov.bot.gitlab.context.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Dmotry Sheyko [25.01.2023]
 */
@Getter
@Setter
@AllArgsConstructor
public class IdAndStatusIssue {

    private long id;
    private long twoId;
    private long projectId;
    private IssueState status;

}