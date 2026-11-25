package com.unibus.backend.controller;

import com.unibus.backend.client.ViaCepClient;
import com.unibus.backend.dto.CepResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint REST para consulta de endereço por CEP ({@code /api/cep}).
 */
@RestController
@RequestMapping("/api/cep")
public class CepController {

    private final ViaCepClient viaCepClient;

    public CepController(ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    /** GET /api/cep/{cep}: consulta endereço pelo CEP na API ViaCEP (200 OK). */
    @GetMapping("/{cep}")
    public CepResponse buscarPorCep(@PathVariable("cep") String cep) {
        return viaCepClient.buscarPorCep(cep);
    }
}
