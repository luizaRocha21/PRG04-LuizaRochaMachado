package com.unibus.backend.dto;

import java.util.List;

/**
 * Estrutura padrão de resposta para erros HTTP (4xx).
 *
 * @param status   código de status HTTP da resposta
 * @param erro     descrição resumida do erro
 * @param detalhes lista de mensagens específicas de validação; pode ser vazia
 */
public record ErroResponse(int status, String erro, List<String> detalhes) {
}
