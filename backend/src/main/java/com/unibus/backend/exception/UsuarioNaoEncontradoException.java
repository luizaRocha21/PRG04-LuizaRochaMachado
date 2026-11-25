package com.unibus.backend.exception;

/**
 * Lançada quando nenhum usuário existe com o id informado.
 * É convertida em resposta HTTP 404 (Not Found) pelo {@link GlobalExceptionHandler}.
 */
public class UsuarioNaoEncontradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UsuarioNaoEncontradoException(Long id) {
        super("Usuário com id " + id + " não encontrado.");
    }
}
