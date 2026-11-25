package com.unibus.backend.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Libera o CORS para que o frontend (outro domínio, ex.: Vercel) consiga chamar a API pelo navegador.
 *
 * <p>As origens permitidas vêm da propriedade {@code app.cors.allowed-origins}, preenchida pela
 * variável de ambiente {@code CORS_ALLOWED_ORIGINS} (lista separada por vírgulas). Padrões como
 * {@code https://*.vercel.app} também são aceitos.</p>
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] origensPermitidas;

    public CorsConfig(@Value("${app.cors.allowed-origins}") String origens) {
        // Separa por vírgula e descarta espaços/entradas vazias (ex.: "a.com, b.com,")
        this.origensPermitidas = Arrays.stream(origens.split(","))
                .map(String::trim)
                .filter(origem -> !origem.isEmpty())
                .toArray(String[]::new);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(origensPermitidas)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600); // o navegador pode reutilizar o preflight por 1 hora
    }
}
