package com.unibus.backend.dto;

import com.unibus.backend.model.Perfil;
import com.unibus.backend.model.StatusUsuario;
import com.unibus.backend.model.Usuario;

/**
 * DTO de saída que representa um usuário nas respostas da API, desacoplando
 * o contrato JSON da entidade de persistência {@link Usuario}.
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
