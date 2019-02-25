package com.ruubel.massfollow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
