package com.unibus.backend.client;

import com.unibus.backend.dto.CepResponse;
import com.unibus.backend.exception.CepInvalidoException;
import com.unibus.backend.exception.CepNaoEncontradoException;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Cliente HTTP reativo para consulta de endereços na API externa ViaCEP utilizando {@link WebClient}.
 */
@Service
public class ViaCepClient {

    private static final Pattern PADRAO_CEP = Pattern.compile("^\\d{8}$");

    private final WebClient viaCepWebClient;

    public ViaCepClient(WebClient viaCepWebClient) {
        this.viaCepWebClient = viaCepWebClient;
    }

    /**
     * Consulta os dados de endereço correspondentes ao CEP informado.
     *
     * @param cep CEP com ou sem máscara (hífen e espaços são removidos)
     * @return dados do endereço correspondente
     * @throws CepInvalidoException      se o CEP não possuir exatamente 8 dígitos numéricos
     * @throws CepNaoEncontradoException se o CEP não existir na base da ViaCEP
     */
    public CepResponse buscarPorCep(String cep) {
        String cepLimpo = (cep == null) ? "" : cep.replace("-", "").trim();
        if (!PADRAO_CEP.matcher(cepLimpo).matches()) {
            throw new CepInvalidoException(cep);
        }

        CepResponse resposta = viaCepWebClient.get()
                .uri("/ws/{cep}/json/", cepLimpo)
                .retrieve()
                .bodyToMono(CepResponse.class)
                .block();

        if (resposta == null || resposta.isInexistente() || resposta.cep() == null) {
            throw new CepNaoEncontradoException(cepLimpo);
        }

        return new CepResponse(
                resposta.cep(),
                resposta.logradouro(),
                resposta.complemento(),
                resposta.bairro(),
                resposta.localidade(),
                resposta.uf()
        );
    }
}
