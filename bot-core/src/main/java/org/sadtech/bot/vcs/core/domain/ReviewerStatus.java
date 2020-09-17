package org.sadtech.bot.vcs.core.domain;

import lombok.Getter;

/**
 * TODO: Добавить комментарий енума.
 *
 * @author upagge [01.02.2020]
 */
@Getter
public enum ReviewerStatus {

    NEEDS_WORK("'NEEDS WORK'"),
    APPROVED("'APPROVED'"),
    UNAPPROVED("'UNAPPROVED'");

    private String value;

    ReviewerStatus(String value) {
        this.value = value;
    }

}
