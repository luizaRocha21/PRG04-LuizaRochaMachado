package com.unibus.backend.dto;

import com.unibus.backend.model.Perfil;
import com.unibus.backend.model.StatusUsuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.Locale;

/**
 * DTO de entrada para criação (POST) e atualização (PUT) de usuários.
 *
 * <p>Os dados são normalizados no construtor compacto antes da execução do Bean Validation:
 * remoção de espaços extremidades no nome e conversão do e-mail para letras minúsculas.</p>
 *
 * <p>As anotações de um mesmo campo compartilham a mesma mensagem para garantir o retorno
 * de uma única mensagem consolidada por campo inválido.</p>
 */
public record UsuarioRequest(

        @NotBlank(message = UsuarioRequest.MSG_NOME)
        @Size(min = 2, max = 100, message = UsuarioRequest.MSG_NOME)
        String nome,

        @NotBlank(message = UsuarioRequest.MSG_EMAIL)
        @Size(max = 254, message = UsuarioRequest.MSG_EMAIL)
        @Pattern(regexp = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$", message = UsuarioRequest.MSG_EMAIL)
        String email,

        @NotNull(message = UsuarioRequest.MSG_PERFIL)
        Perfil perfil,

        @NotNull(message = UsuarioRequest.MSG_STATUS)
        StatusUsuario status
) {

    static final String MSG_NOME =
            "O campo 'nome' é obrigatório e deve ter entre 2 e 100 caracteres.";
    static final String MSG_EMAIL =
            "O campo 'email' é obrigatório e deve ter um formato válido (até 254 caracteres).";
    static final String MSG_PERFIL =
            "O campo 'perfil' é obrigatório e deve ser um dos valores: estudante, motorista, admin.";
    static final String MSG_STATUS =
            "O campo 'status' é obrigatório e deve ser um dos valores: ativo, inativo, pendente.";

    /** Construtor compacto: normaliza nome e e-mail antes de a validação ser executada. */
    public UsuarioRequest {
        nome = (nome == null) ? null : nome.trim();
        email = (email == null) ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}
