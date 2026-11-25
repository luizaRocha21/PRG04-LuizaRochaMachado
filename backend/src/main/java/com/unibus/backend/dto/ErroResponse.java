package com.unibus.backend.dto;

import java.util.List;

/**
 * Corpo padrão devolvido em respostas de erro (4xx).
 *
 * @param status   código HTTP da resposta (ex.: 404)
 * @param erro     resumo do problema em português
 * @param detalhes lista de mensagens específicas (ex.: um item por campo inválido); pode ser vazia
 */
public record ErroResponse(int status, String erro, List<String> detalhes) {
}
