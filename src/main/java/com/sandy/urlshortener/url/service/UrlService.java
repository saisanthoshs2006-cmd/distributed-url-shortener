package com.sandy.urlshortener.url.service;

import com.sandy.urlshortener.common.Base62Encoder;
import com.sandy.urlshortener.common.UrlMapper;
import com.sandy.urlshortener.exception.InvalidUrlException;
import com.sandy.urlshortener.exception.UrlNotFoundException;
import com.sandy.urlshortener.url.dto.CreateUrlRequest;
import com.sandy.urlshortener.url.dto.UrlResponse;
import com.sandy.urlshortener.url.dto.UrlStatsResponse;
import com.sandy.urlshortener.url.entity.Url;
import com.sandy.urlshortener.url.repository.UrlRepository;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlService {

    private final UrlRepository repository;
    private final Base62Encoder encoder;
    private final UrlMapper mapper;
    private final Clock clock;

    public UrlService(UrlRepository repository, Base62Encoder encoder, UrlMapper mapper, Clock clock) {
        this.repository = repository;
        this.encoder = encoder;
        this.mapper = mapper;
        this.clock = clock;
    }

    @Transactional
    public UrlResponse create(CreateUrlRequest request) {
        validateOriginalUrl(request.originalUrl());
        Instant now = Instant.now(clock);
        if (request.expiresAt() != null && !request.expiresAt().isAfter(now)) {
            throw new InvalidUrlException("expiresAt must be in the future");
        }

        Url url = repository.saveAndFlush(new Url(request.originalUrl(), now, request.expiresAt()));
        url.setShortCode(encoder.encode(url.getId()));
        return mapper.toResponse(repository.save(url));
    }

    @Transactional
    public String resolve(String shortCode) {
        Url url = activeUrl(shortCode);
        repository.recordClick(url.getId(), Instant.now(clock));
        return url.getOriginalUrl();
    }

    @Transactional(readOnly = true)
    public UrlResponse get(String shortCode) {
        return mapper.toResponse(existingUrl(shortCode));
    }

    @Transactional
    public void delete(String shortCode) {
        existingUrl(shortCode).deactivate();
    }

    @Transactional(readOnly = true)
    public UrlStatsResponse stats(String shortCode) {
        return mapper.toStats(existingUrl(shortCode));
    }

    private Url activeUrl(String shortCode) {
        Url url = existingUrl(shortCode);
        if (!url.isActive() || isExpired(url)) {
            throw new UrlNotFoundException(shortCode);
        }
        return url;
    }

    private Url existingUrl(String shortCode) {
        return repository.findByShortCode(shortCode).orElseThrow(() -> new UrlNotFoundException(shortCode));
    }

    private boolean isExpired(Url url) {
        return url.getExpiresAt() != null && !url.getExpiresAt().isAfter(Instant.now(clock));
    }

    private void validateOriginalUrl(String originalUrl) {
        try {
            URI uri = URI.create(originalUrl);
            String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
            if (!(scheme.equals("http") || scheme.equals("https")) || uri.getHost() == null) {
                throw new InvalidUrlException("originalUrl must be a valid HTTP or HTTPS URL");
            }
        } catch (IllegalArgumentException exception) {
            throw new InvalidUrlException("originalUrl must be a valid HTTP or HTTPS URL");
        }
    }
}
