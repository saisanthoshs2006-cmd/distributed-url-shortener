package com.sandy.urlshortener.url.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;

@Entity
@Table(name = "urls")
public class Url {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", unique = true, length = 16)
    private String shortCode;

    @Column(name = "original_url", nullable = false, length = 2048)
    private String originalUrl;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "click_count", nullable = false)
    private long clickCount;

    @Column(name = "last_accessed_at")
    private Instant lastAccessedAt;

    @Column(nullable = false)
    private boolean active;

    @Version
    private long version;

    protected Url() {
    }

    public Url(String originalUrl, Instant createdAt, Instant expiresAt) {
        this.originalUrl = originalUrl;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.active = true;
    }

    public Long getId() { return id; }
    public String getShortCode() { return shortCode; }
    public String getOriginalUrl() { return originalUrl; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExpiresAt() { return expiresAt; }
    public long getClickCount() { return clickCount; }
    public Instant getLastAccessedAt() { return lastAccessedAt; }
    public boolean isActive() { return active; }

    public void setShortCode(String shortCode) { this.shortCode = shortCode; }
    public void deactivate() { this.active = false; }
}
