package com.unibus.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.unibus.backend.model.Perfil;
import com.unibus.backend.model.StatusUsuario;
import com.unibus.backend.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Testes da camada de persistência ({@link UsuarioRepository}) com banco H2 em memória ({@link DataJpaTest}).
 */
@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario salvar(String nome, String email, Perfil perfil, StatusUsuario status) {
        return usuarioRepository.saveAndFlush(new Usuario(nome, email, perfil, status));
    }

    @Test
    @DisplayName("save gera o id e persiste todos os campos, inclusive os enums")
    void save_deveGerarIdEPersistirOsCampos() {
        Usuario salvo = salvar("Ana Souza", "ana@unibus.com", Perfil.MOTORISTA, StatusUsuario.PENDENTE);

        assertThat(salvo.getId()).isNotNull();

        Usuario lido = usuarioRepository.findById(salvo.getId()).orElseThrow();
        assertThat(lido.getNome()).isEqualTo("Ana Souza");
        assertThat(lido.getEmail()).isEqualTo("ana@unibus.com");
        assertThat(lido.getPerfil()).isEqualTo(Perfil.MOTORISTA);
        assertThat(lido.getStatus()).isEqualTo(StatusUsuario.PENDENTE);
    }

    @Test
    @DisplayName("findAll com Pageable retorna página ordenada por id")
    void findAll_comPageable_deveRetornarPaginaOrdenadaPorId() {
        Usuario primeiro = salvar("Ana", "ana@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO);
        Usuario segundo = salvar("Bruno", "bruno@unibus.com", Perfil.ADMIN, StatusUsuario.ATIVO);
        salvar("Carla", "carla@unibus.com", Perfil.MOTORISTA, StatusUsuario.ATIVO);

        Page<Usuario> pagina = usuarioRepository.findAll(PageRequest.of(0, 2, Sort.by("id")));

        assertThat(pagina.getContent()).extracting(Usuario::getId)
                .containsExactly(primeiro.getId(), segundo.getId());
        assertThat(pagina.getTotalElements()).isEqualTo(3);
        assertThat(pagina.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("existsByEmail retorna true somente para e-mails já cadastrados")
    void existsByEmail_deveIndicarSeOEmailExiste() {
        salvar("Ana", "ana@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO);

        assertThat(usuarioRepository.existsByEmail("ana@unibus.com")).isTrue();
        assertThat(usuarioRepository.existsByEmail("outra@unibus.com")).isFalse();
    }

    @Test
    @DisplayName("existsByEmailAndIdNot ignora o próprio usuário e considera os demais")
    void existsByEmailAndIdNot_deveIgnorarOProprioUsuario() {
        Usuario ana = salvar("Ana", "ana@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO);
        Usuario bruno = salvar("Bruno", "bruno@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO);

        assertThat(usuarioRepository.existsByEmailAndIdNot("ana@unibus.com", ana.getId())).isFalse();
        assertThat(usuarioRepository.existsByEmailAndIdNot("ana@unibus.com", bruno.getId())).isTrue();
    }

    @Test
    @DisplayName("o banco recusa dois usuários com o mesmo e-mail (restrição de unicidade)")
    void save_comEmailRepetido_deveLancarDataIntegrityViolationException() {
        salvar("Ana", "ana@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO);

        assertThatThrownBy(() -> salvar("Outra Ana", "ana@unibus.com", Perfil.MOTORISTA, StatusUsuario.ATIVO))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("deleteById remove o registro")
    void deleteById_deveRemoverORegistro() {
        Usuario salvo = salvar("Ana", "ana@unibus.com", Perfil.ESTUDANTE, StatusUsuario.ATIVO);

        usuarioRepository.deleteById(salvo.getId());
        usuarioRepository.flush();

        assertThat(usuarioRepository.existsById(salvo.getId())).isFalse();
    }
}
