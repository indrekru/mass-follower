package com.ruubel.massfollow.config;

import com.rollbar.notifier.Rollbar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.TimeZone;

import static com.rollbar.notifier.config.ConfigBuilder.withAccessToken;

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

    @Bean
    public Rollbar rollbar() {
        Rollbar rollbar = Rollbar.init(withAccessToken("34cea2fc2be749e7a2ec60d066286f9f").build());
        return rollbar;
    }
}
