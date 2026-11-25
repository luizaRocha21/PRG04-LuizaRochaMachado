package com.unibus.backend.dto;

import com.unibus.backend.model.Perfil;
import com.unibus.backend.model.StatusUsuario;
import com.unibus.backend.model.Usuario;

/**
 * Representação de um usuário devolvida pela API.
 *
 * <p>Exemplo de JSON: {@code {"id":1,"nome":"Ana","email":"ana@unibus.com",
 * "perfil":"estudante","status":"ativo"}}. A entidade JPA nunca é exposta diretamente.</p>
 */
public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Perfil perfil,
        StatusUsuario status
) {

    /** Converte a entidade {@link Usuario} no DTO de resposta. */
    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getPerfil(),
                usuario.getStatus()
        );
    }
}
