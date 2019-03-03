package com.ruubel.massfollow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.TimeZone;

@Configuration
public class AppConfiguration {

    public AppConfiguration() {
        configureUTC();
    }

    public void configureUTC() {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
    }

    @Bean
    public ConfigParams configParams() throws Exception {
        return new ConfigParams();
    }

    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(4);
        executor.setThreadNamePrefix("default_task_executor_thread");
        executor.initialize();
        return executor;
    }
}
