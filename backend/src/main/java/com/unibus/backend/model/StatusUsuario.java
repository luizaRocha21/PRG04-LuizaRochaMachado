package com.unibus.backend.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Situação cadastral de um usuário do UniBus.
 *
 * <p>Assim como em {@link Perfil}, o JSON usa minúsculas ("ativo", "inativo", "pendente")
 * e o banco guarda o nome da constante.</p>
 */
public enum StatusUsuario {

    ATIVO("ativo"),
    INATIVO("inativo"),
    PENDENTE("pendente");

    private final String valor;

    StatusUsuario(String valor) {
        this.valor = valor;
    }

    /** Texto usado no JSON (serialização e desserialização). */
    @JsonValue
    public String getValor() {
        return valor;
    }
}
