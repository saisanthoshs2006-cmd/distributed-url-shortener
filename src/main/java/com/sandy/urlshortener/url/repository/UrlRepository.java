package com.sandy.urlshortener.url.repository;

import com.sandy.urlshortener.url.entity.Url;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UrlRepository extends JpaRepository<Url, Long> {

    Optional<Url> findByShortCode(String shortCode);

    @Modifying
    @Query("update Url u set u.clickCount = u.clickCount + 1, u.lastAccessedAt = :accessedAt where u.id = :id")
    int recordClick(@Param("id") Long id, @Param("accessedAt") Instant accessedAt);
}
