package com.sandy.urlshortener.url.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sandy.urlshortener.common.Base62Encoder;
import com.sandy.urlshortener.common.ShortUrlProperties;
import com.sandy.urlshortener.common.UrlMapper;
import com.sandy.urlshortener.exception.InvalidUrlException;
import com.sandy.urlshortener.exception.UrlNotFoundException;
import com.sandy.urlshortener.url.dto.CreateUrlRequest;
import com.sandy.urlshortener.url.entity.Url;
import com.sandy.urlshortener.url.repository.UrlRepository;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-11T12:00:00Z");

    @Mock
    private UrlRepository repository;

    private UrlService service;

    @BeforeEach
    void setUp() {
        UrlMapper mapper = new UrlMapper(new ShortUrlProperties(URI.create("http://localhost:8080")));
        service = new UrlService(repository, new Base62Encoder(), mapper, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void rejectsExpiredCreationRequest() {
        CreateUrlRequest request = new CreateUrlRequest("https://example.com", NOW.minusSeconds(1));

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(InvalidUrlException.class)
                .hasMessage("expiresAt must be in the future");
    }

    @Test
    void rejectsNonHttpUrl() {
        assertThatThrownBy(() -> service.create(new CreateUrlRequest("javascript:alert(1)", null)))
                .isInstanceOf(InvalidUrlException.class);
    }

    @Test
    void redirectsActiveUrlAndRecordsClickAtomically() {
        Url url = new Url("https://example.com", NOW.minusSeconds(10), null);
        url.setShortCode("1");
        setId(url, 1L);
        when(repository.findByShortCode("1")).thenReturn(Optional.of(url));
        when(repository.recordClick(eq(1L), eq(NOW))).thenReturn(1);

        assertThat(service.resolve("1")).isEqualTo("https://example.com");
        verify(repository).recordClick(1L, NOW);
    }

    @Test
    void doesNotRedirectExpiredUrl() {
        Url url = new Url("https://example.com", NOW.minusSeconds(10), NOW);
        url.setShortCode("1");
        when(repository.findByShortCode("1")).thenReturn(Optional.of(url));

        assertThatThrownBy(() -> service.resolve("1"))
                .isInstanceOf(UrlNotFoundException.class);
    }

    private void setId(Url url, Long id) {
        try {
            var field = Url.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(url, id);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError(exception);
        }
    }
}
