package com.tsc.bitbucketbot.dto.bitbucket;

import com.tsc.bitbucketbot.domain.BitbucketUserRole;
import lombok.Data;

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
