package com.unibus.backend.controller;

import com.unibus.backend.dto.StatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint raiz da API ({@code GET /}).
 *
 * <p>Responde sempre 200 com o estado do serviço. O Render o usa como "health check" para saber
 * que a aplicação subiu, e serve também como teste rápido no navegador. Não consulta o banco,
 * então é leve e pode ser chamado com frequência.</p>
 */
@RestController
public class StatusController {

    /** GET /: informa que a API está no ar. */
    @GetMapping("/")
    public StatusResponse status() {
        return new StatusResponse("online", "UniBus API");
    }
}
