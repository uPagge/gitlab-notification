package com.tsc.bitbucketbot.repository.jpa;

import com.tsc.bitbucketbot.domain.entity.TechInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TechInfoRepository extends JpaRepository<TechInfo, Long> {

}
