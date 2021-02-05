package org.sadtech.bot.gitlab.context.domain.filter;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sadtech.bot.gitlab.context.domain.MergeRequestState;

import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MergeRequestFilter {

    private Long assignee;
    private Set<MergeRequestState> states;

}
