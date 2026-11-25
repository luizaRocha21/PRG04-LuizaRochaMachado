package com.unibus.backend.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.unibus.backend.dto.CepResponse;
import com.unibus.backend.exception.CepInvalidoException;
import com.unibus.backend.exception.CepNaoEncontradoException;
import java.net.URI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

/**
 * Testes unitários de {@link ViaCepClient} utilizando {@link ExchangeFunction} em memória,
 * sem dependência de rede externa.
 */
class ViaCepClientTest {

    private static ViaCepClient criarClienteComRespostaJson(HttpStatus status, String json) {
        ExchangeFunction exchangeFunction = request -> Mono.just(
                ClientResponse.create(status)
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(json)
                        .build()
        );
        WebClient webClient = WebClient.builder()
                .baseUrl("https://viacep.com.br")
                .exchangeFunction(exchangeFunction)
                .build();
        return new ViaCepClient(webClient);
    }

    @Test
    @DisplayName("buscarPorCep retorna os dados do endereço ignorando campos extras da ViaCEP")
    void buscarPorCep_deveRetornarEnderecoQuandoCepExiste() {
        String jsonViaCep = """
                {
                  "cep": "01001-000",
                  "logradouro": "Praça da Sé",
                  "complemento": "lado ímpar",
                  "unidade": "",
                  "bairro": "Sé",
                  "localidade": "São Paulo",
                  "uf": "SP",
                  "estado": "São Paulo",
                  "regiao": "Sudeste",
                  "ibge": "3550308",
                  "gia": "1004",
                  "ddd": "11",
                  "siafi": "7107"
                }
                """;
        ViaCepClient client = criarClienteComRespostaJson(HttpStatus.OK, jsonViaCep);

        CepResponse resultado = client.buscarPorCep("01001-000");

        assertThat(resultado.cep()).isEqualTo("01001-000");
        assertThat(resultado.logradouro()).isEqualTo("Praça da Sé");
        assertThat(resultado.complemento()).isEqualTo("lado ímpar");
        assertThat(resultado.bairro()).isEqualTo("Sé");
        assertThat(resultado.localidade()).isEqualTo("São Paulo");
        assertThat(resultado.uf()).isEqualTo("SP");
        assertThat(resultado.erro()).isNull();
    }

    @Test
    @DisplayName("buscarPorCep lança CepInvalidoException quando o formato do CEP é inválido")
    void buscarPorCep_deveLancarCepInvalidoExceptionQuandoFormatoInvalido() {
        ViaCepClient client = criarClienteComRespostaJson(HttpStatus.OK, "{}");

        assertThatThrownBy(() -> client.buscarPorCep("123"))
                .isInstanceOf(CepInvalidoException.class)
                .hasMessageContaining("123");
    }

    @Test
    @DisplayName("buscarPorCep lança CepNaoEncontradoException quando a ViaCEP retorna erro true")
    void buscarPorCep_deveLancarCepNaoEncontradoExceptionQuandoViaCepRetornaErro() {
        ViaCepClient client = criarClienteComRespostaJson(HttpStatus.OK, """
                { "erro": true }
                """);

        assertThatThrownBy(() -> client.buscarPorCep("99999999"))
                .isInstanceOf(CepNaoEncontradoException.class)
                .hasMessage("CEP '99999999' não encontrado.");
    }

    @Test
    @DisplayName("buscarPorCep propaga WebClientException quando ocorre falha de comunicação")
    void buscarPorCep_devePropagarWebClientExceptionEmFalhaDeRede() {
        ExchangeFunction falhaRede = request -> Mono.error(
                new WebClientRequestException(
                        new RuntimeException("Falha de conexão"),
                        HttpMethod.GET,
                        URI.create("https://viacep.com.br/ws/01001000/json/"),
                        HttpHeaders.EMPTY
                )
        );
        WebClient webClient = WebClient.builder()
                .baseUrl("https://viacep.com.br")
                .exchangeFunction(falhaRede)
                .build();
        ViaCepClient client = new ViaCepClient(webClient);

        assertThatThrownBy(() -> client.buscarPorCep("01001000"))
                .isInstanceOf(WebClientException.class);
    }
}
