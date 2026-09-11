package com.sandy.urlshortener.common;

import com.sandy.urlshortener.url.dto.UrlResponse;
import com.sandy.urlshortener.url.dto.UrlStatsResponse;
import com.sandy.urlshortener.url.entity.Url;
import org.springframework.stereotype.Component;

@Component
public class UrlMapper {

    private final ShortUrlProperties properties;

    public UrlMapper(ShortUrlProperties properties) {
        this.properties = properties;
    }

    public UrlResponse toResponse(Url url) {
        String baseUrl = properties.baseUrl().toString().replaceAll("/$", "");
        return new UrlResponse(url.getShortCode(), baseUrl + "/" + url.getShortCode(), url.getOriginalUrl(), url.getCreatedAt(), url.getExpiresAt());
    }

    public UrlStatsResponse toStats(Url url) {
        return new UrlStatsResponse(url.getShortCode(), url.getClickCount(), url.getCreatedAt(), url.getLastAccessedAt());
    }
}
