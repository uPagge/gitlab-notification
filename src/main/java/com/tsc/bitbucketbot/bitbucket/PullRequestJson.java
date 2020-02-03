package com.tsc.bitbucketbot.bitbucket;

import lombok.Data;

import java.util.List;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [30.01.2020]
 */
@Data
public class PullRequestJson {

    private Long id;
    private Integer version;
    private PullRequestState state;
    private String title;
    private String description;
    private LinkJson links;
    private UserDecisionJson author;
    private List<UserDecisionJson> reviewers;

}
