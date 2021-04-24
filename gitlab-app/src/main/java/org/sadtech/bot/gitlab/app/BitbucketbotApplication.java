package org.sadtech.bot.gitlab.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"org.sadtech.bot.gitlab.data.jpa"})
@SpringBootApplication(scanBasePackages = "org.sadtech.bot.gitlab")
@EntityScan(basePackages = {"org.sadtech.bot.gitlab.context.domain.entity"})
public class BitbucketbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(BitbucketbotApplication.class, args);
    }

}
