package com.tsc.bitbucketbot.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class IdAndStatusPr {

    private Long id;
    private PullRequestStatus status;

}
