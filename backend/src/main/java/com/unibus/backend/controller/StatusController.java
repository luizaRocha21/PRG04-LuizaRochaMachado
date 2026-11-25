package com.unibus.backend.controller;

import com.unibus.backend.dto.StatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint raiz da API ({@code GET /}) utilizado para verificação de disponibilidade (health check).
 */
@RestController
public class StatusController {

    /** Retorna o estado operacional do serviço. */
    @GetMapping("/")
    public StatusResponse status() {
        return new StatusResponse("online", "UniBus API");
    }
}
