package com.unibus.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO de saída com os dados de endereço retornados pela consulta de CEP (ViaCEP).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CepResponse(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        String localidade,
        String uf,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        Boolean erro
) {

    public CepResponse(
            String cep,
            String logradouro,
            String complemento,
            String bairro,
            String localidade,
            String uf) {
        this(cep, logradouro, complemento, bairro, localidade, uf, null);
    }

    /** Indica se a API ViaCEP sinalizou o CEP como inexistente. */
    public boolean isInexistente() {
        return Boolean.TRUE.equals(erro);
    }
}
