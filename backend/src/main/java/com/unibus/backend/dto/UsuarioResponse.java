package com.unibus.backend.dto;

import com.unibus.backend.model.Perfil;
import com.unibus.backend.model.StatusUsuario;

/**
 * DTO de saída que representa um usuário nas respostas da API, desacoplando
 * o contrato JSON da entidade de persistência.
 */
public record UsuarioResponse(
        Long id,
        String nome,
        String email,
        Perfil perfil,
        StatusUsuario status
) {
}
