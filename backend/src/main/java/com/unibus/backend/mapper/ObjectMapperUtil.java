package com.unibus.backend.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

/**
 * Componente utilitário para conversão entre entidades JPA e DTOs utilizando {@link ObjectMapper}.
 */
@Component
public class ObjectMapperUtil {

    private final ObjectMapper objectMapper;

    public ObjectMapperUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /** Converte um objeto de origem para uma nova instância da classe de destino. */
    public <D> D map(Object origem, Class<D> destino) {
        return objectMapper.convertValue(origem, destino);
    }

    /** Converte uma lista de objetos de origem para uma lista da classe de destino. */
    public <D> List<D> mapList(List<?> origem, Class<D> destino) {
        return origem.stream()
                .map(item -> map(item, destino))
                .toList();
    }

    /** Atualiza os campos de uma instância existente a partir dos dados do objeto de origem. */
    public <T> T atualizar(Object origem, T destino) {
        return objectMapper.updateValue(destino, origem);
    }
}
