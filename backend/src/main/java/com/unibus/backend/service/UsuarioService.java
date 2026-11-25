package com.unibus.backend.service;

import com.unibus.backend.dto.UsuarioRequest;
import com.unibus.backend.dto.UsuarioResponse;
import com.unibus.backend.exception.EmailJaCadastradoException;
import com.unibus.backend.exception.UsuarioNaoEncontradoException;
import com.unibus.backend.model.Usuario;
import com.unibus.backend.repository.UsuarioRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Camada de negócio dos usuários: aplica as regras (id existente, e-mail único) e
 * orquestra o acesso ao {@link UsuarioRepository}.
 *
 * <p>Recebe e devolve DTOs, de modo que a entidade JPA não vaza para a camada web.
 * Cada método público roda em uma transação (somente leitura nas consultas).</p>
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    /** Injeção de dependência pelo construtor (o Spring fornece o repositório). */
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /** Lista todos os usuários, ordenados pelo id. */
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll(Sort.by("id")).stream()
                .map(UsuarioResponse::de)
                .toList();
    }

    /**
     * Busca um usuário pelo id.
     *
     * @throws UsuarioNaoEncontradoException se não existir
     */
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return UsuarioResponse.de(buscarEntidade(id));
    }

    /**
     * Cadastra um novo usuário.
     *
     * @throws EmailJaCadastradoException se o e-mail já estiver em uso
     */
    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        // Regra de negócio: o e-mail identifica o usuário, então não pode se repetir.
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        Usuario usuario = new Usuario(request.nome(), request.email(), request.perfil(), request.status());
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    /**
     * Substitui todos os dados de um usuário existente (semântica do PUT).
     *
     * @throws UsuarioNaoEncontradoException se o id não existir
     * @throws EmailJaCadastradoException    se o novo e-mail pertencer a OUTRO usuário
     */
    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscarEntidade(id);

        // O próprio usuário pode manter o e-mail; só é conflito se outro registro já o usa.
        if (usuarioRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new EmailJaCadastradoException(request.email());
        }

        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setPerfil(request.perfil());
        usuario.setStatus(request.status());
        return UsuarioResponse.de(usuarioRepository.save(usuario));
    }

    /**
     * Remove um usuário.
     *
     * @throws UsuarioNaoEncontradoException se o id não existir
     */
    @Transactional
    public void remover(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNaoEncontradoException(id);
        }
        usuarioRepository.deleteById(id);
    }

    /** Busca a entidade ou lança a exceção que vira 404. Reaproveitado por buscar e atualizar. */
    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }
}
