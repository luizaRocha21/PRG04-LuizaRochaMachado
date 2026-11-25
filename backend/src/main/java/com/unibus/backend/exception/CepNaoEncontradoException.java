package com.unibus.backend.exception;

/**
 * Lançada quando o CEP consultado não existe na base da ViaCEP.
 * Convertida em resposta HTTP 404 (Not Found) pelo {@link GlobalExceptionHandler}.
 */
public class CepNaoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CepNaoEncontradoException(String cep) {
        super("CEP '" + cep + "' não encontrado.");
    }
}
