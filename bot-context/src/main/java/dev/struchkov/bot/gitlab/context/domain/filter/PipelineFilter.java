package dev.struchkov.bot.gitlab.context.domain.filter;

import dev.struchkov.bot.gitlab.context.domain.entity.Pipeline;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Объект фильтра для {@link Pipeline}.
 *
 * @author upagge 08.02.2021
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PipelineFilter {

    private LocalDateTime lessThanCreatedDate;

}
