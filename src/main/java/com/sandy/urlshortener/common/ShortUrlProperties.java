package com.sandy.urlshortener.common;

import java.net.URI;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "urlshortener")
public record ShortUrlProperties(URI baseUrl) {
}
