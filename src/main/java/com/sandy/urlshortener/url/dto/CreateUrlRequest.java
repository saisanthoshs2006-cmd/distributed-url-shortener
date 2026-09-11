package com.sandy.urlshortener.url.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record CreateUrlRequest(
        @NotBlank(message = "originalUrl is required")
        @Size(max = 2048, message = "originalUrl must be at most 2048 characters")
        String originalUrl,
        Instant expiresAt) {
}
