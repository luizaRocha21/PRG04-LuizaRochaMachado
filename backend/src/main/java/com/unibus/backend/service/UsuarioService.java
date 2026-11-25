package com.unibus.backend.service;

import com.unibus.backend.dto.UsuarioRequest;
import com.unibus.backend.dto.UsuarioResponse;
import com.unibus.backend.exception.EmailJaCadastradoException;
import com.unibus.backend.exception.UsuarioNaoEncontradoException;
import com.unibus.backend.mapper.ObjectMapperUtil;
import com.unibus.backend.model.Usuario;
import com.unibus.backend.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Camada de negócio dos usuários: aplica as regras (id existente, e-mail único),
 * converte entidades e DTOs via {@link ObjectMapperUtil} e orquestra o acesso ao
 * {@link UsuarioRepository}.
 */
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ObjectMapperUtil objectMapperUtil;

    public UsuarioService(UsuarioRepository usuarioRepository, ObjectMapperUtil objectMapperUtil) {
        this.usuarioRepository = usuarioRepository;
        this.objectMapperUtil = objectMapperUtil;
    }

    /** Lista os usuários de forma paginada. */
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarTodos(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(usuario -> objectMapperUtil.map(usuario, UsuarioResponse.class));
    }

    /**
     * Busca um usuário pelo identificador.
     *
     * @throws UsuarioNaoEncontradoException se o registro não existir
     */
    @Transactional(readOnly = true)
    public UsuarioResponse buscarPorId(Long id) {
        return objectMapperUtil.map(buscarEntidade(id), UsuarioResponse.class);
    }

    /**
     * Cadastra um novo usuário garantindo a unicidade do e-mail.
     *
     * @throws EmailJaCadastradoException se o e-mail já estiver em uso
     */
    @Transactional
    public UsuarioResponse criar(UsuarioRequest request) {
        if (usuarioRepository.existsByEmail(request.email())) {
            throw new EmailJaCadastradoException(request.email());
        }

        Usuario usuario = objectMapperUtil.map(request, Usuario.class);
        Usuario salvo = usuarioRepository.save(usuario);
        return objectMapperUtil.map(salvo, UsuarioResponse.class);
    }

    /**
     * Substitui todos os dados de um usuário existente (semântica PUT).
     *
     * @throws UsuarioNaoEncontradoException se o identificador não existir
     * @throws EmailJaCadastradoException    se o novo e-mail pertencer a outro usuário
     */
    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest request) {
        Usuario usuario = buscarEntidade(id);

        // Permite manter o próprio e-mail; bloqueia apenas conflito com outro registro
        if (usuarioRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new EmailJaCadastradoException(request.email());
        }

        objectMapperUtil.atualizar(request, usuario);
        Usuario salvo = usuarioRepository.save(usuario);
        return objectMapperUtil.map(salvo, UsuarioResponse.class);
    }

    /**
     * Remove um usuário pelo identificador.
     *
     * @throws UsuarioNaoEncontradoException se o identificador não existir
     */
    @Transactional
    public void remover(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new UsuarioNaoEncontradoException(id);
        }
        usuarioRepository.deleteById(id);
    }

    /** Recupera a entidade pelo identificador ou lança {@link UsuarioNaoEncontradoException}. */
    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }
}
