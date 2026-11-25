package com.unibus.backend.exception;

/**
 * Lançada quando o CEP informado não possui formato válido (8 dígitos numéricos).
 * Convertida em resposta HTTP 400 (Bad Request) pelo {@link GlobalExceptionHandler}.
 */
public class CepInvalidoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CepInvalidoException(String cep) {
        super("O CEP '" + cep + "' é inválido. Informe um CEP com 8 dígitos numéricos.");
    }
}
