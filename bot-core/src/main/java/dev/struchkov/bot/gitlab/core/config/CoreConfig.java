package dev.struchkov.bot.gitlab.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ForkJoinPool;

@Configuration
public class CoreConfig {

    @Bean("parserPool")
    public ForkJoinPool parserPool() {
        return new ForkJoinPool(4);
    }

}
