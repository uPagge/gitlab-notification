package dev.struchkov.bot.gitlab.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = {"dev.struchkov.bot.gitlab.data.jpa"})
@SpringBootApplication(scanBasePackages = "dev.struchkov.bot.gitlab")
@EntityScan(basePackages = {"dev.struchkov.bot.gitlab.context.domain.entity"})
public class GitLabBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(GitLabBotApplication.class, args);
    }

}
