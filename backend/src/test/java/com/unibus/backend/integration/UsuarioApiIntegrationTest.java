package com.unibus.backend.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.unibus.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Teste de integração da API: requisições HTTP reais (MockMvc) atravessando controller, serviço,
 * repositório e um banco H2 em memória, sem nenhum mock.
 *
 * <p>Complementa os testes por camada: aqui se confirma que as peças funcionam juntas
 * (por exemplo, a regra de e-mail único e a configuração de CORS).</p>
 */
@SpringBootTest(properties = "app.cors.allowed-origins=http://localhost:3000")
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UsuarioApiIntegrationTest {

    private static final String URL = "/api/usuarios";
    private static final String ORIGEM_FRONTEND = "http://localhost:3000";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /** Começa cada teste com a tabela vazia. */
    @BeforeEach
    void limparBanco() {
        usuarioRepository.deleteAll();
    }

    private static String json(String nome, String email, String perfil, String status) {
        return """
                { "nome": "%s", "email": "%s", "perfil": "%s", "status": "%s" }
                """.formatted(nome, email, perfil, status);
    }

    /** Cadastra um usuário pela API e devolve o id, lido do cabeçalho Location. */
    private long criarPelaApi(String nome, String email, String perfil, String status) throws Exception {
        MvcResult resultado = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(nome, email, perfil, status)))
                .andExpect(status().isCreated())
                .andReturn();
        String location = resultado.getResponse().getHeader("Location");
        assertThat(location).isNotNull();
        return Long.parseLong(location.substring(location.lastIndexOf('/') + 1));
    }

    @Test
    @DisplayName("fluxo completo: criar, buscar, listar, atualizar e excluir")
    void fluxoCompleto() throws Exception {
        // POST -> 201
        long id = criarPelaApi("Ana Souza", "ana@unibus.com", "estudante", "ativo");

        // GET por id -> 200
        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Ana Souza"))
                .andExpect(jsonPath("$.email").value("ana@unibus.com"))
                .andExpect(jsonPath("$.perfil").value("estudante"))
                .andExpect(jsonPath("$.status").value("ativo"));

        // GET lista -> 200 com 1 item
        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(id));

        // PUT -> 200 com os novos dados
        mockMvc.perform(put(URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Ana Souza Lima", "ana.lima@unibus.com", "admin", "inativo")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.nome").value("Ana Souza Lima"))
                .andExpect(jsonPath("$.email").value("ana.lima@unibus.com"))
                .andExpect(jsonPath("$.perfil").value("admin"))
                .andExpect(jsonPath("$.status").value("inativo"));

        // a alteração foi mesmo gravada
        mockMvc.perform(get(URL + "/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Souza Lima"))
                .andExpect(jsonPath("$.status").value("inativo"));

        // DELETE -> 204; depois disso o recurso não existe mais
        mockMvc.perform(delete(URL + "/" + id)).andExpect(status().isNoContent());
        mockMvc.perform(get(URL + "/" + id)).andExpect(status().isNotFound());
        mockMvc.perform(delete(URL + "/" + id)).andExpect(status().isNotFound());
        assertThat(usuarioRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST grava nome sem espaços nas pontas e e-mail em minúsculas")
    void post_deveNormalizarOsDados() throws Exception {
        long id = criarPelaApi("  Bruno Lima  ", "  BRUNO@UniBus.com ", "motorista", "pendente");

        mockMvc.perform(get(URL + "/" + id))
                .andExpect(jsonPath("$.nome").value("Bruno Lima"))
                .andExpect(jsonPath("$.email").value("bruno@unibus.com"));
    }

    @Test
    @DisplayName("GET lista os usuários em ordem crescente de id")
    void get_deveListarEmOrdemDeId() throws Exception {
        long primeiro = criarPelaApi("Ana", "ana@unibus.com", "estudante", "ativo");
        long segundo = criarPelaApi("Bruno", "bruno@unibus.com", "motorista", "ativo");
        long terceiro = criarPelaApi("Carla", "carla@unibus.com", "admin", "ativo");

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id").value(primeiro))
                .andExpect(jsonPath("$[1].id").value(segundo))
                .andExpect(jsonPath("$[2].id").value(terceiro));
    }

    @Test
    @DisplayName("GET por id inexistente retorna 404")
    void get_comIdInexistente_deveRetornar404() throws Exception {
        mockMvc.perform(get(URL + "/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST com dados inválidos retorna 400 e não grava nada")
    void post_comDadosInvalidos_deveRetornar400() throws Exception {
        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("A", "email-invalido", "estudante", "ativo")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detalhes", hasSize(2)));

        assertThat(usuarioRepository.count()).isZero();
    }

    @Test
    @DisplayName("POST com e-mail repetido (mesmo com maiúsculas) retorna 409 e não duplica o registro")
    void post_comEmailRepetido_deveRetornar409() throws Exception {
        criarPelaApi("Ana", "ana@unibus.com", "estudante", "ativo");

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Outra Ana", "ANA@unibus.com", "motorista", "ativo")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));

        assertThat(usuarioRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("PUT com o e-mail de outro usuário retorna 409")
    void put_comEmailDeOutroUsuario_deveRetornar409() throws Exception {
        criarPelaApi("Ana", "ana@unibus.com", "estudante", "ativo");
        long idBruno = criarPelaApi("Bruno", "bruno@unibus.com", "motorista", "ativo");

        mockMvc.perform(put(URL + "/" + idBruno)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Bruno", "ana@unibus.com", "motorista", "ativo")))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT mantendo o próprio e-mail é permitido (200)")
    void put_mantendoOProprioEmail_deveRetornar200() throws Exception {
        long id = criarPelaApi("Ana", "ana@unibus.com", "estudante", "ativo");

        mockMvc.perform(put(URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Ana Atualizada", "ana@unibus.com", "estudante", "ativo")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Atualizada"));
    }

    @Test
    @DisplayName("PUT em id inexistente retorna 404")
    void put_comIdInexistente_deveRetornar404() throws Exception {
        mockMvc.perform(put(URL + "/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json("Ana", "ana@unibus.com", "estudante", "ativo")))
                .andExpect(status().isNotFound());
    }

    // ---------------------------------------------------------------------------------------
    // CORS: o frontend roda em outro domínio e precisa ser autorizado
    // ---------------------------------------------------------------------------------------
    @Test
    @DisplayName("CORS: preflight de uma origem autorizada recebe 200 e os cabeçalhos Access-Control-*")
    void cors_preflightDeOrigemAutorizada() throws Exception {
        // É o que o navegador envia antes de um POST com corpo JSON vindo de outro domínio
        mockMvc.perform(options(URL)
                        .header("Origin", ORIGEM_FRONTEND)
                        .header("Access-Control-Request-Method", "POST")
                        .header("Access-Control-Request-Headers", "content-type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", ORIGEM_FRONTEND))
                .andExpect(header().exists("Access-Control-Allow-Methods"));
    }

    @Test
    @DisplayName("CORS: requisição de uma origem não autorizada é rejeitada com 403")
    void cors_origemNaoAutorizadaERejeitada() throws Exception {
        mockMvc.perform(get(URL).header("Origin", "https://site-nao-autorizado.example"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("CORS: GET de uma origem autorizada recebe o cabeçalho Access-Control-Allow-Origin")
    void cors_getDeOrigemAutorizada() throws Exception {
        mockMvc.perform(get(URL).header("Origin", ORIGEM_FRONTEND))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", ORIGEM_FRONTEND));
    }
}
