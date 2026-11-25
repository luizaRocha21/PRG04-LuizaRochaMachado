package com.unibus.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.unibus.backend.dto.UsuarioRequest;
import com.unibus.backend.dto.UsuarioResponse;
import com.unibus.backend.exception.EmailJaCadastradoException;
import com.unibus.backend.exception.UsuarioNaoEncontradoException;
import com.unibus.backend.model.Perfil;
import com.unibus.backend.model.StatusUsuario;
import com.unibus.backend.model.Usuario;
import com.unibus.backend.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Testes unitários do {@link UsuarioService} com {@link UsuarioRepository} simulado via Mockito.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    /** Instancia uma entidade com identificador preenchido para simular registro persistido. */
    private static Usuario usuarioPersistido(Long id, String nome, String email, Perfil perfil, StatusUsuario status) {
        Usuario usuario = new Usuario(nome, email, perfil, status);
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }

    private static UsuarioRequest request() {
        return new UsuarioRequest("Ana Souza", "ana@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO);
    }

    @Test
    @DisplayName("listarTodos converte as entidades em DTOs, ordenadas pelo id")
    void listarTodos_deveRetornarDtosOrdenadosPeloId() {
        when(usuarioRepository.findAll(Sort.by("id"))).thenReturn(List.of(
                usuarioPersistido(1L, "Ana Souza", "ana@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO),
                usuarioPersistido(2L, "Bruno Lima", "bruno@unibus.com", Perfil.MOTORISTA, StatusUsuario.INATIVO)));

        List<UsuarioResponse> resultado = usuarioService.listarTodos();

        assertThat(resultado).containsExactly(
                new UsuarioResponse(1L, "Ana Souza", "ana@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO),
                new UsuarioResponse(2L, "Bruno Lima", "bruno@unibus.com", Perfil.MOTORISTA, StatusUsuario.INATIVO));
    }

    @Test
    @DisplayName("listarTodos retorna lista vazia quando não há usuários")
    void listarTodos_deveRetornarListaVaziaQuandoNaoHaUsuarios() {
        when(usuarioRepository.findAll(Sort.by("id"))).thenReturn(List.of());

        assertThat(usuarioService.listarTodos()).isEmpty();
    }

    @Test
    @DisplayName("buscarPorId retorna o usuário quando o id existe")
    void buscarPorId_deveRetornarOUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(
                usuarioPersistido(1L, "Ana Souza", "ana@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO)));

        UsuarioResponse resultado = usuarioService.buscarPorId(1L);

        assertThat(resultado)
                .isEqualTo(new UsuarioResponse(1L, "Ana Souza", "ana@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO));
    }

    @Test
    @DisplayName("buscarPorId lança UsuarioNaoEncontradoException quando o id não existe")
    void buscarPorId_deveLancarExcecaoQuandoNaoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorId(99L))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessage("Usuário com id 99 não encontrado.");
    }

    @Test
    @DisplayName("criar grava o usuário e devolve o DTO com o id gerado")
    void criar_deveSalvarEDevolverOUsuarioCriado() {
        when(usuarioRepository.existsByEmail("ana@unibus.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacao -> {
            Usuario salvo = invocacao.getArgument(0);
            ReflectionTestUtils.setField(salvo, "id", 10L);
            return salvo;
        });

        UsuarioResponse resultado = usuarioService.criar(request());

        assertThat(resultado)
                .isEqualTo(new UsuarioResponse(10L, "Ana Souza", "ana@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("Ana Souza");
        assertThat(captor.getValue().getEmail()).isEqualTo("ana@unibus.com");
        assertThat(captor.getValue().getPerfil()).isEqualTo(Perfil.ESTUDANTE);
        assertThat(captor.getValue().getStatus()).isEqualTo(StatusUsuario.ATIVO);
    }

    @Test
    @DisplayName("criar lança EmailJaCadastradoException e não grava quando o e-mail já existe")
    void criar_deveLancarExcecaoQuandoEmailJaExiste() {
        when(usuarioRepository.existsByEmail("ana@unibus.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.criar(request()))
                .isInstanceOf(EmailJaCadastradoException.class)
                .hasMessage("Já existe um usuário cadastrado com o e-mail 'ana@unibus.com'.");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("atualizar substitui todos os campos do usuário existente")
    void atualizar_deveSubstituirTodosOsCampos() {
        Usuario existente = usuarioPersistido(5L, "Nome Antigo", "antigo@unibus.com", Perfil.ESTUDANTE, StatusUsuario.PENDENTE);
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByEmailAndIdNot("novo@unibus.com", 5L)).thenReturn(false);
        when(usuarioRepository.save(existente)).thenReturn(existente);

        UsuarioRequest novosDados =
                new UsuarioRequest("Nome Novo", "novo@unibus.com", Perfil.MOTORISTA, StatusUsuario.ATIVO);

        UsuarioResponse resultado = usuarioService.atualizar(5L, novosDados);

        assertThat(resultado)
                .isEqualTo(new UsuarioResponse(5L, "Nome Novo", "novo@unibus.com", Perfil.MOTORISTA, StatusUsuario.ATIVO));
        assertThat(existente.getNome()).isEqualTo("Nome Novo");
        assertThat(existente.getEmail()).isEqualTo("novo@unibus.com");
        assertThat(existente.getPerfil()).isEqualTo(Perfil.MOTORISTA);
        assertThat(existente.getStatus()).isEqualTo(StatusUsuario.ATIVO);
    }

    @Test
    @DisplayName("atualizar lança UsuarioNaoEncontradoException quando o id não existe")
    void atualizar_deveLancarExcecaoQuandoNaoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.atualizar(99L, request()))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessage("Usuário com id 99 não encontrado.");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("atualizar lança EmailJaCadastradoException quando o e-mail é de outro usuário")
    void atualizar_deveLancarExcecaoQuandoEmailPertenceAOutroUsuario() {
        Usuario existente = usuarioPersistido(5L, "Ana Souza", "ana@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO);
        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existsByEmailAndIdNot("ana@unibus.com", 5L)).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.atualizar(5L, request()))
                .isInstanceOf(EmailJaCadastradoException.class);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("remover exclui o usuário quando o id existe")
    void remover_deveExcluirQuandoExiste() {
        when(usuarioRepository.existsById(3L)).thenReturn(true);

        usuarioService.remover(3L);

        verify(usuarioRepository).deleteById(3L);
    }

    @Test
    @DisplayName("remover lança UsuarioNaoEncontradoException quando o id não existe")
    void remover_deveLancarExcecaoQuandoNaoExiste() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.remover(99L))
                .isInstanceOf(UsuarioNaoEncontradoException.class)
                .hasMessage("Usuário com id 99 não encontrado.");

        verify(usuarioRepository, never()).deleteById(any());
    }
}
