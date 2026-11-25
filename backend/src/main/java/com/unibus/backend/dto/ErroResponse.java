package com.unibus.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

/**
 * Estrutura padrão de resposta para erros HTTP (4xx e 5xx).
 *
 * @param status   código de status HTTP da resposta
 * @param erro     descrição resumida do erro
 * @param detalhes lista de mensagens específicas de validação; pode ser vazia
 * @param campos   mapa opcional associando cada campo inválido à respectiva mensagem de erro
 */
public record ErroResponse(
        int status,
        String erro,
        List<String> detalhes,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        Map<String, String> campos
) {

    public ErroResponse(int status, String erro, List<String> detalhes) {
        this(status, erro, detalhes, Map.of());
    }
}
