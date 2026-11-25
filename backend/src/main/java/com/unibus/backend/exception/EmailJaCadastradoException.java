package com.unibus.backend.exception;

/**
 * Lançada quando se tenta cadastrar (ou atualizar para) um e-mail que já pertence a outro usuário.
 * É convertida em resposta HTTP 409 (Conflict) pelo {@link GlobalExceptionHandler}.
 */
public class EmailJaCadastradoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmailJaCadastradoException(String email) {
        super("Já existe um usuário cadastrado com o e-mail '" + email + "'.");
    }
}
