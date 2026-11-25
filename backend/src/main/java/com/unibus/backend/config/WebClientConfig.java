package com.unibus.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuração do {@link WebClient} para consumo da API externa ViaCEP.
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient viaCepWebClient(
            @Value("${app.viacep.base-url:https://viacep.com.br}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
