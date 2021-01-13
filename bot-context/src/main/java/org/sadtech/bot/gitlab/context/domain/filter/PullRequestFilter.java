package org.sadtech.bot.gitlab.context.domain.filter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PullRequestFilter {

    private Long bitbucketId;
    private Long bitbucketRepositoryId;

}
