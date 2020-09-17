package org.sadtech.bot.vcs.bitbucket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "org.sadtech.bot.vcs.core.repository.jpa")
@SpringBootApplication(scanBasePackages = "org.sadtech.bot.vcs")
@EntityScan(basePackages = "org.sadtech.bot.vcs.core.domain.entity")
public class BitbucketbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(BitbucketbotApplication.class, args);
    }

}
