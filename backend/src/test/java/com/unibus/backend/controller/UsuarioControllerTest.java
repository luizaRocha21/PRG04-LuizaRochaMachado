package com.unibus.backend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.unibus.backend.dto.UsuarioRequest;
import com.unibus.backend.dto.UsuarioResponse;
import com.unibus.backend.exception.EmailJaCadastradoException;
import com.unibus.backend.exception.UsuarioNaoEncontradoException;
import com.unibus.backend.model.Perfil;
import com.unibus.backend.model.StatusUsuario;
import com.unibus.backend.service.UsuarioService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Testes da camada web isolada ({@link UsuarioController}) utilizando {@link WebMvcTest}.
 *
 * <p>Valida o contrato HTTP (rotas, verbos, códigos de status, cabeçalhos e serialização JSON)
 * com {@link UsuarioService} simulado via mock.</p>
 */
@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    private static final String URL = "/api/usuarios";

    private static final String JSON_VALIDO = """
            {
              "nome": "Ana Souza",
              "email": "ana.souza@unibus.com",
              "perfil": "estudante",
              "status": "ativo"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    private static UsuarioResponse ana() {
        return new UsuarioResponse(1L, "Ana Souza", "ana.souza@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO);
    }
    @Nested
    @DisplayName("GET /api/usuarios (listar)")
    class Listar {

        @Test
        @DisplayName("retorna 200 com a lista de usuários em JSON")
        void deveRetornar200ComAListaDeUsuarios() throws Exception {
            given(usuarioService.listarTodos()).willReturn(List.of(
                    ana(),
                    new UsuarioResponse(2L, "Bruno Lima", "bruno@unibus.com", Perfil.MOTORISTA, StatusUsuario.INATIVO)));

            mockMvc.perform(get(URL))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].nome").value("Ana Souza"))
                    .andExpect(jsonPath("$[0].email").value("ana.souza@unibus.com"))
                    // enums trafegam em minúsculas, como o frontend espera
                    .andExpect(jsonPath("$[0].perfil").value("estudante"))
                    .andExpect(jsonPath("$[0].status").value("ativo"))
                    .andExpect(jsonPath("$[1].perfil").value("motorista"))
                    .andExpect(jsonPath("$[1].status").value("inativo"));
        }

        @Test
        @DisplayName("retorna 200 com lista vazia quando não há usuários")
        void deveRetornar200ComListaVazia() throws Exception {
            given(usuarioService.listarTodos()).willReturn(List.of());

            mockMvc.perform(get(URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/usuarios/{id} (buscarPorId)")
    class BuscarPorId {

        @Test
        @DisplayName("retorna 200 com o usuário encontrado")
        void deveRetornar200ComOUsuario() throws Exception {
            given(usuarioService.buscarPorId(1L)).willReturn(ana());

            mockMvc.perform(get(URL + "/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Ana Souza"))
                    .andExpect(jsonPath("$.email").value("ana.souza@unibus.com"))
                    .andExpect(jsonPath("$.perfil").value("estudante"))
                    .andExpect(jsonPath("$.status").value("ativo"));
        }

        @Test
        @DisplayName("retorna 404 quando o usuário não existe")
        void deveRetornar404QuandoNaoExiste() throws Exception {
            given(usuarioService.buscarPorId(99L)).willThrow(new UsuarioNaoEncontradoException(99L));

            mockMvc.perform(get(URL + "/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.erro").value("Usuário com id 99 não encontrado."))
                    .andExpect(jsonPath("$.detalhes", hasSize(0)));
        }

        @Test
        @DisplayName("retorna 400 quando o id não é numérico")
        void deveRetornar400QuandoIdNaoENumerico() throws Exception {
            mockMvc.perform(get(URL + "/abc"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.detalhes[0]").value("O valor informado para 'id' não é válido."));

            verifyNoInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("POST /api/usuarios (criar)")
    class Criar {

        @Test
        @DisplayName("retorna 201 Created com o corpo e o cabeçalho Location")
        void deveRetornar201ComLocation() throws Exception {
            given(usuarioService.criar(any(UsuarioRequest.class))).willReturn(ana());

            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                    .andExpect(status().isCreated())
                    .andExpect(header().string("Location", endsWith("/api/usuarios/1")))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Ana Souza"))
                    .andExpect(jsonPath("$.perfil").value("estudante"))
                    .andExpect(jsonPath("$.status").value("ativo"));
        }

        @Test
        @DisplayName("normaliza nome (trim) e e-mail (minúsculas) antes de chamar o serviço")
        void deveNormalizarNomeEEmail() throws Exception {
            given(usuarioService.criar(any(UsuarioRequest.class))).willReturn(ana());
            String json = """
                    {
                      "nome": "   Ana Souza   ",
                      "email": "  ANA.Souza@UniBus.COM ",
                      "perfil": "admin",
                      "status": "pendente"
                    }
                    """;

            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isCreated());

            ArgumentCaptor<UsuarioRequest> captor = ArgumentCaptor.forClass(UsuarioRequest.class);
            verify(usuarioService).criar(captor.capture());
            assertThat(captor.getValue().nome()).isEqualTo("Ana Souza");
            assertThat(captor.getValue().email()).isEqualTo("ana.souza@unibus.com");
            assertThat(captor.getValue().perfil()).isEqualTo(Perfil.ADMIN);
            assertThat(captor.getValue().status()).isEqualTo(StatusUsuario.PENDENTE);
        }

        @Test
        @DisplayName("retorna 400 listando os campos inválidos")
        void deveRetornar400QuandoCamposSaoInvalidos() throws Exception {
            String json = """
                    { "nome": "A", "email": "sem-arroba", "perfil": "estudante", "status": "ativo" }
                    """;

            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.erro").value("Dados inválidos."))
                    .andExpect(jsonPath("$.detalhes", hasSize(2)))
                    .andExpect(jsonPath("$.detalhes", hasItem(
                            "O campo 'nome' é obrigatório e deve ter entre 2 e 100 caracteres.")))
                    .andExpect(jsonPath("$.detalhes", hasItem(
                            "O campo 'email' é obrigatório e deve ter um formato válido (até 254 caracteres).")));

            verify(usuarioService, never()).criar(any());
        }

        @Test
        @DisplayName("retorna 400 com uma mensagem por campo quando o corpo está vazio")
        void deveRetornar400QuandoFaltamTodosOsCampos() throws Exception {
            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.detalhes", hasSize(4)));

            verify(usuarioService, never()).criar(any());
        }

        @Test
        @DisplayName("retorna 400 quando o perfil não é um valor conhecido")
        void deveRetornar400QuandoPerfilEInvalido() throws Exception {
            String json = """
                    { "nome": "Ana Souza", "email": "ana@unibus.com", "perfil": "professor", "status": "ativo" }
                    """;

            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.erro").value("Corpo da requisição inválido."));

            verify(usuarioService, never()).criar(any());
        }

        @Test
        @DisplayName("retorna 400 quando o JSON está malformado")
        void deveRetornar400QuandoJsonEstaMalformado() throws Exception {
            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{ nome: Ana"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.erro").value("Corpo da requisição inválido."));

            verify(usuarioService, never()).criar(any());
        }

        @Test
        @DisplayName("retorna 409 quando o e-mail já está cadastrado")
        void deveRetornar409QuandoEmailJaExiste() throws Exception {
            given(usuarioService.criar(any(UsuarioRequest.class)))
                    .willThrow(new EmailJaCadastradoException("ana.souza@unibus.com"));

            mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409))
                    .andExpect(jsonPath("$.erro")
                            .value("Já existe um usuário cadastrado com o e-mail 'ana.souza@unibus.com'."));
        }

        @Test
        @DisplayName("retorna 415 quando o Content-Type não é JSON")
        void deveRetornar415QuandoContentTypeNaoEJson() throws Exception {
            mockMvc.perform(post(URL).contentType(MediaType.TEXT_PLAIN).content(JSON_VALIDO))
                    .andExpect(status().isUnsupportedMediaType());

            verifyNoInteractions(usuarioService);
        }
    }

    @Nested
    @DisplayName("PUT /api/usuarios/{id} (atualizar)")
    class Atualizar {

        @Test
        @DisplayName("retorna 200 com o usuário atualizado")
        void deveRetornar200ComOUsuarioAtualizado() throws Exception {
            UsuarioResponse atualizado =
                    new UsuarioResponse(1L, "Ana Souza", "ana.souza@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO);
            given(usuarioService.atualizar(eq(1L), any(UsuarioRequest.class))).willReturn(atualizado);

            mockMvc.perform(put(URL + "/1").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nome").value("Ana Souza"))
                    .andExpect(jsonPath("$.status").value("ativo"));

            ArgumentCaptor<UsuarioRequest> captor = ArgumentCaptor.forClass(UsuarioRequest.class);
            verify(usuarioService).atualizar(eq(1L), captor.capture());
            assertThat(captor.getValue())
                    .isEqualTo(new UsuarioRequest("Ana Souza", "ana.souza@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO));
        }

        @Test
        @DisplayName("retorna 404 quando o usuário não existe")
        void deveRetornar404QuandoNaoExiste() throws Exception {
            given(usuarioService.atualizar(eq(99L), any(UsuarioRequest.class)))
                    .willThrow(new UsuarioNaoEncontradoException(99L));

            mockMvc.perform(put(URL + "/99").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.erro").value("Usuário com id 99 não encontrado."));
        }

        @Test
        @DisplayName("retorna 400 quando faltam campos (PUT exige a representação completa)")
        void deveRetornar400QuandoCorpoEstaIncompleto() throws Exception {
            String json = """
                    { "nome": "Ana Souza", "email": "ana.souza@unibus.com" }
                    """;

            mockMvc.perform(put(URL + "/1").contentType(MediaType.APPLICATION_JSON).content(json))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.detalhes", hasSize(2)))
                    .andExpect(jsonPath("$.detalhes", hasItem(
                            "O campo 'perfil' é obrigatório e deve ser um dos valores: estudante, motorista, admin.")))
                    .andExpect(jsonPath("$.detalhes", hasItem(
                            "O campo 'status' é obrigatório e deve ser um dos valores: ativo, inativo, pendente.")));

            verify(usuarioService, never()).atualizar(any(), any());
        }

        @Test
        @DisplayName("retorna 409 quando o e-mail pertence a outro usuário")
        void deveRetornar409QuandoEmailEDeOutroUsuario() throws Exception {
            given(usuarioService.atualizar(eq(1L), any(UsuarioRequest.class)))
                    .willThrow(new EmailJaCadastradoException("ana.souza@unibus.com"));

            mockMvc.perform(put(URL + "/1").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.status").value(409));
        }
    }

    @Nested
    @DisplayName("DELETE /api/usuarios/{id} (remover)")
    class Remover {

        @Test
        @DisplayName("retorna 204 No Content, sem corpo")
        void deveRetornar204SemCorpo() throws Exception {
            mockMvc.perform(delete(URL + "/1"))
                    .andExpect(status().isNoContent())
                    .andExpect(content().string(""));

            verify(usuarioService).remover(1L);
        }

        @Test
        @DisplayName("retorna 404 quando o usuário não existe")
        void deveRetornar404QuandoNaoExiste() throws Exception {
            willThrow(new UsuarioNaoEncontradoException(99L)).given(usuarioService).remover(99L);

            mockMvc.perform(delete(URL + "/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.erro").value("Usuário com id 99 não encontrado."));
        }
    }

    @Nested
    @DisplayName("Verbos não suportados")
    class VerbosNaoSuportados {

        @Test
        @DisplayName("PATCH /api/usuarios/{id} retorna 405 Method Not Allowed")
        void patchDeveRetornar405() throws Exception {
            mockMvc.perform(patch(URL + "/1").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("POST /api/usuarios/{id} retorna 405 Method Not Allowed")
        void postEmRecursoEspecificoDeveRetornar405() throws Exception {
            mockMvc.perform(post(URL + "/1").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("DELETE /api/usuarios (coleção inteira) retorna 405 Method Not Allowed")
        void deleteNaColecaoDeveRetornar405() throws Exception {
            mockMvc.perform(delete(URL))
                    .andExpect(status().isMethodNotAllowed());
        }
    }
}
