package dev.struchkov.bot.gitlab.data.jpa;

import dev.struchkov.bot.gitlab.context.domain.entity.MergeRequestForDiscussion;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

/**
 * @author upagge [31.01.2020]
 */

public interface MergeRequestForDiscussionJpaRepository extends JpaRepositoryImplementation<MergeRequestForDiscussion, Long> {

}
