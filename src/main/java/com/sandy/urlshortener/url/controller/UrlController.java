package com.sandy.urlshortener.url.controller;

import com.sandy.urlshortener.url.dto.CreateUrlRequest;
import com.sandy.urlshortener.url.dto.UrlResponse;
import com.sandy.urlshortener.url.dto.UrlStatsResponse;
import com.sandy.urlshortener.url.service.UrlService;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UrlController {

    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    @PostMapping("/api/v1/urls")
    public ResponseEntity<UrlResponse> create(@Valid @RequestBody CreateUrlRequest request) {
        UrlResponse response = service.create(request);
        return ResponseEntity.created(URI.create(response.shortUrl())).body(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        return ResponseEntity.status(302).location(URI.create(service.resolve(shortCode))).build();
    }

    @GetMapping("/api/v1/urls/{shortCode}")
    public UrlResponse get(@PathVariable String shortCode) {
        return service.get(shortCode);
    }

    @DeleteMapping("/api/v1/urls/{shortCode}")
    public ResponseEntity<Void> delete(@PathVariable String shortCode) {
        service.delete(shortCode);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/v1/urls/{shortCode}/stats")
    public UrlStatsResponse stats(@PathVariable String shortCode) {
        return service.stats(shortCode);
    }
}
