package org.sadtech.bot.vcs.bitbucket.app.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author upagge
 */
@Getter
@Setter
@Component
@ConfigurationProperties("bitbucketbot.scheduler.comment.settings")
public class CommentSchedulerProperty {

    /**
     * Количество пустых комментариев подряд, после которого поиск останавливается
     */
    private Integer noCommentCount;

    /**
     * Количество комментариев в пачке сканирования
     */
    private Integer commentCount;

}
