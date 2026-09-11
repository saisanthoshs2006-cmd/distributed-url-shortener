package com.sandy.urlshortener.url.dto;

import java.time.Instant;

public record UrlStatsResponse(
        String shortCode,
        long totalClicks,
        Instant createdAt,
        Instant lastAccessedAt) {
}
