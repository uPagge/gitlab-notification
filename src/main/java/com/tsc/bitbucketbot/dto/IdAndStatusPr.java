package com.tsc.bitbucketbot.dto;

import com.tsc.bitbucketbot.domain.PullRequestStatus;
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
