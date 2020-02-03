package com.tsc.bitbucketbot.repository;

import com.tsc.bitbucketbot.domain.entity.PullRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

/**
 * TODO: Добавить описание интерфейса.
 *
 * @author upagge [31.01.2020]
 */
public interface PullRequestsRepository extends JpaRepository<PullRequest, Long> {

}
