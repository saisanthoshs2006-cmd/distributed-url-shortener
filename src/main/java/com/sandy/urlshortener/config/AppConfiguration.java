package com.sandy.urlshortener.config;

import com.sandy.urlshortener.common.ShortUrlProperties;
import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ShortUrlProperties.class)
public class AppConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
