package com.sandy.urlshortener.url.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sandy.urlshortener.url.service.UrlService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UrlController.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService service;

    @Test
    void redirectsWithTemporaryRedirectStatus() throws Exception {
        when(service.resolve("abc")).thenReturn("https://example.com/target");

        mockMvc.perform(get("/abc"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://example.com/target"));
    }
}
