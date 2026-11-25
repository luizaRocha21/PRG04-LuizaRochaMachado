package com.unibus.backend.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.unibus.backend.client.ViaCepClient;
import com.unibus.backend.dto.CepResponse;
import com.unibus.backend.exception.CepInvalidoException;
import com.unibus.backend.exception.CepNaoEncontradoException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Testes da camada web para {@link CepController} utilizando {@link WebMvcTest}.
 */
@WebMvcTest(CepController.class)
class CepControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ViaCepClient viaCepClient;

    @Test
    @DisplayName("GET /api/cep/{cep} retorna 200 OK com os dados do endereço sem expor o campo erro")
    void buscarPorCep_deveRetornar200ComDadosDoEndereco() throws Exception {
        given(viaCepClient.buscarPorCep("01001000")).willReturn(
                new CepResponse("01001-000", "Praça da Sé", "lado ímpar", "Sé", "São Paulo", "SP")
        );

        mockMvc.perform(get("/api/cep/01001000"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.cep").value("01001-000"))
                .andExpect(jsonPath("$.logradouro").value("Praça da Sé"))
                .andExpect(jsonPath("$.complemento").value("lado ímpar"))
                .andExpect(jsonPath("$.bairro").value("Sé"))
                .andExpect(jsonPath("$.localidade").value("São Paulo"))
                .andExpect(jsonPath("$.uf").value("SP"))
                .andExpect(jsonPath("$.erro").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/cep/{cep} retorna 400 Bad Request quando o CEP tem formato inválido")
    void buscarPorCep_deveRetornar400QuandoCepEInvalido() throws Exception {
        given(viaCepClient.buscarPorCep("123")).willThrow(new CepInvalidoException("123"));

        mockMvc.perform(get("/api/cep/123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro")
                        .value("O CEP '123' é inválido. Informe um CEP com 8 dígitos numéricos."));
    }

    @Test
    @DisplayName("GET /api/cep/{cep} retorna 404 Not Found quando o CEP não existe na ViaCEP")
    void buscarPorCep_deveRetornar404QuandoCepNaoExiste() throws Exception {
        given(viaCepClient.buscarPorCep("99999999")).willThrow(new CepNaoEncontradoException("99999999"));

        mockMvc.perform(get("/api/cep/99999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("CEP '99999999' não encontrado."));
    }

    @Test
    @DisplayName("GET /api/cep/{cep} retorna 502 Bad Gateway quando ocorre falha na API externa")
    void buscarPorCep_deveRetornar502QuandoApiExternaFalha() throws Exception {
        given(viaCepClient.buscarPorCep("01001000")).willThrow(
                WebClientResponseException.create(
                        503,
                        "Service Unavailable",
                        HttpHeaders.EMPTY,
                        new byte[0],
                        StandardCharsets.UTF_8
                )
        );

        mockMvc.perform(get("/api/cep/01001000"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.erro").value("Erro ao consultar o serviço externo de CEP."));
    }
}
