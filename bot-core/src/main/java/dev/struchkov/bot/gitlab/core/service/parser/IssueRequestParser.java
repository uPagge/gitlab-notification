package dev.struchkov.bot.gitlab.core.service.parser;

import dev.struchkov.bot.gitlab.context.domain.IssueState;

import java.util.Set;

public class IssueRequestParser {
private static final Set<IssueState> OLD_STATUSES = Set.of(
        IssueState.OPENED, IssueState.CLOSED);
}
