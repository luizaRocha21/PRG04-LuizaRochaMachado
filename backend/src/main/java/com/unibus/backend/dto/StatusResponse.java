package com.unibus.backend.dto;

/**
 * Resposta do endpoint raiz ({@code GET /}), usado como verificação de saúde pelo Render.
 *
 * @param status situação da API (sempre "online" quando ela está respondendo)
 * @param servico nome do serviço
 */
public record StatusResponse(String status, String servico) {
}
