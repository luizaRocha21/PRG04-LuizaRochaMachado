package com.unibus.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da API do UniBus.
 *
 * <p>Estrutura em camadas (pacotes abaixo de {@code com.unibus.backend}):</p>
 * <ul>
 *   <li>{@code controller}: endpoints REST (HTTP);</li>
 *   <li>{@code service}: regras de negócio;</li>
 *   <li>{@code repository}: acesso ao banco (Spring Data JPA);</li>
 *   <li>{@code model}: entidades JPA e enums;</li>
 *   <li>{@code dto}: objetos de entrada/saída da API;</li>
 *   <li>{@code exception}: exceções de negócio e tradução para respostas HTTP;</li>
 *   <li>{@code config}: configurações (CORS).</li>
 * </ul>
 */
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
