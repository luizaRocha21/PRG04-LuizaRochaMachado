package com.unibus.backend.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.unibus.backend.dto.UsuarioRequest;
import com.unibus.backend.dto.UsuarioResponse;
import com.unibus.backend.model.Perfil;
import com.unibus.backend.model.StatusUsuario;
import com.unibus.backend.model.Usuario;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

/**
 * Testes unitários de {@link ObjectMapperUtil} para conversão entre {@link Usuario},
 * {@link UsuarioRequest} e {@link UsuarioResponse}.
 */
class ObjectMapperUtilTest {

    private ObjectMapperUtil objectMapperUtil;

    @BeforeEach
    void setUp() {
        objectMapperUtil = new ObjectMapperUtil(new ObjectMapper());
    }

    private static Usuario usuarioPersistido(Long id, String nome, String email, Perfil perfil, StatusUsuario status) {
        Usuario usuario = new Usuario(nome, email, perfil, status);
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }

    @Test
    @DisplayName("map converte UsuarioRequest para Usuario preservando campos normalizados e mantendo id nulo")
    void map_deveConverterRequestParaEntidadeComIdNulo() {
        UsuarioRequest request = new UsuarioRequest(
                "  Ana Souza  ",
                "  ANA.SOUZA@UniBus.com ",
                Perfil.ESTUDANTE,
                StatusUsuario.ATIVO
        );

        Usuario entidade = objectMapperUtil.map(request, Usuario.class);

        assertThat(entidade.getId()).isNull();
        assertThat(entidade.getNome()).isEqualTo("Ana Souza");
        assertThat(entidade.getEmail()).isEqualTo("ana.souza@unibus.com");
        assertThat(entidade.getPerfil()).isEqualTo(Perfil.ESTUDANTE);
        assertThat(entidade.getStatus()).isEqualTo(StatusUsuario.ATIVO);
    }

    @Test
    @DisplayName("map converte Usuario para UsuarioResponse preservando id e demais atributos")
    void map_deveConverterEntidadeParaResponse() {
        Usuario entidade = usuarioPersistido(7L, "Bruno Lima", "bruno@unibus.com", Perfil.MOTORISTA, StatusUsuario.PENDENTE);

        UsuarioResponse response = objectMapperUtil.map(entidade, UsuarioResponse.class);

        assertThat(response).isEqualTo(
                new UsuarioResponse(7L, "Bruno Lima", "bruno@unibus.com", Perfil.MOTORISTA, StatusUsuario.PENDENTE)
        );
    }

    @Test
    @DisplayName("mapList converte lista de Usuario para lista de UsuarioResponse")
    void mapList_deveConverterListaDeEntidadesParaListaDeResponses() {
        List<Usuario> entidades = List.of(
                usuarioPersistido(1L, "Ana Souza", "ana@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO),
                usuarioPersistido(2L, "Carla Dias", "carla@unibus.com", Perfil.ADMIN, StatusUsuario.INATIVO)
        );

        List<UsuarioResponse> responses = objectMapperUtil.mapList(entidades, UsuarioResponse.class);

        assertThat(responses).containsExactly(
                new UsuarioResponse(1L, "Ana Souza", "ana@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO),
                new UsuarioResponse(2L, "Carla Dias", "carla@unibus.com", Perfil.ADMIN, StatusUsuario.INATIVO)
        );
    }

    @Test
    @DisplayName("atualizar copia dados do UsuarioRequest para Usuario existente preservando o id original")
    void atualizar_deveAtualizarCamposPreservandoIdOriginal() {
        Usuario existente = usuarioPersistido(15L, "Nome Antigo", "antigo@unibus.com", Perfil.ESTUDANTE, StatusUsuario.PENDENTE);
        UsuarioRequest novosDados = new UsuarioRequest("Nome Novo", "novo@unibus.com", Perfil.ADMIN, StatusUsuario.ATIVO);

        Usuario atualizado = objectMapperUtil.atualizar(novosDados, existente);

        assertThat(atualizado).isSameAs(existente);
        assertThat(existente.getId()).isEqualTo(15L);
        assertThat(existente.getNome()).isEqualTo("Nome Novo");
        assertThat(existente.getEmail()).isEqualTo("novo@unibus.com");
        assertThat(existente.getPerfil()).isEqualTo(Perfil.ADMIN);
        assertThat(existente.getStatus()).isEqualTo(StatusUsuario.ATIVO);
    }
}
