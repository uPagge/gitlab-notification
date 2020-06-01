package org.sadtech.bot.bitbucketbot.dto.bitbucket;

import lombok.Data;
import org.sadtech.bot.bitbucketbot.domain.BitbucketUserRole;

/**
 * TODO: Добавить описание класса.
 *
 * @author upagge [31.01.2020]
 */
@Data
public class UserDecisionJson {

    private UserJson user;
    private BitbucketUserRole role;
    private Boolean approved;
    private UserPullRequestStatus status;

}
