package com.NotesSummary.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {


@Value("${openrouter.api.key}")
private String apikey;
@Value("${openrouter.url}")
private String baseUrl;

    @Bean
    public WebClient openRouterWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apikey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("HTTP-Referer", "https://ainotes.in")
                .defaultHeader("X-Title", "AiNotesApp")
                .build();
    }



}
