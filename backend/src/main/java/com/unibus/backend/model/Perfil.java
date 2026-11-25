package com.unibus.backend.model;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Perfis de acesso possíveis para um usuário do UniBus.
 *
 * <p>Na API os valores trafegam em minúsculas ("estudante", "motorista", "admin"), que é o
 * contrato já usado pelo frontend. No banco é gravado o nome da constante (ESTUDANTE, ...).</p>
 */
public enum Perfil {

    ESTUDANTE("estudante"),
    MOTORISTA("motorista"),
    ADMIN("admin");

    private final String valor;

    Perfil(String valor) {
        this.valor = valor;
    }

    /**
     * Texto usado no JSON. A anotação {@link JsonValue} vale tanto para serializar
     * (enum -> JSON) quanto para desserializar (JSON -> enum).
     */
    @JsonValue
    public String getValor() {
        return valor;
    }
}
