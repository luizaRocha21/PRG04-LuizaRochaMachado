package com.unibus.backend.exception;

import com.unibus.backend.dto.ErroResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientException;

/**
 * Traduz as exceções da aplicação em respostas HTTP com o status code correto e um corpo
 * JSON padronizado ({@link ErroResponse}).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** 404: o id informado não existe. */
    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        return montarResposta(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    /** 404: o CEP consultado não foi encontrado na API externa. */
    @ExceptionHandler(CepNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarCepNaoEncontrado(CepNaoEncontradoException ex) {
        return montarResposta(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    /** 400: o CEP informado possui formato inválido. */
    @ExceptionHandler(CepInvalidoException.class)
    public ResponseEntity<ErroResponse> tratarCepInvalido(CepInvalidoException ex) {
        return montarResposta(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }

    /** 502: falha de comunicação com a API externa ViaCEP. */
    @ExceptionHandler(WebClientException.class)
    public ResponseEntity<ErroResponse> tratarErroWebClient(WebClientException ex) {
        return montarResposta(HttpStatus.BAD_GATEWAY,
                "Erro ao consultar o serviço externo de CEP.",
                List.of());
    }

    /** 409: o e-mail já pertence a outro usuário. */
    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<ErroResponse> tratarEmailJaCadastrado(EmailJaCadastradoException ex) {
        return montarResposta(HttpStatus.CONFLICT, ex.getMessage(), List.of());
    }

    /**
     * 409: o banco recusou o dado por violar uma restrição de unicidade. Cobre a corrida em que
     * duas requisições com o mesmo e-mail passam juntas pela verificação do serviço.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> tratarViolacaoDeIntegridade(DataIntegrityViolationException ex) {
        return montarResposta(HttpStatus.CONFLICT,
                "Os dados informados conflitam com um registro já existente (por exemplo, e-mail repetido).",
                List.of());
    }

    /** 400: algum campo do corpo falhou na validação (@Valid). Identifica cada campo e motivo da falha. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new TreeMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String campo = fieldError.getField();
            String mensagem = fieldError.getDefaultMessage();
            if (mensagem != null) {
                campos.merge(campo, mensagem, (atual, nova) ->
                        atual.contains(nova) ? atual : atual + "; " + nova);
            }
        }

        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErroResponse(HttpStatus.BAD_REQUEST.value(), "Dados inválidos.", detalhes, campos));
    }

    /** 400: corpo JSON malformado ou com valor de enum inválido. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> tratarCorpoIlegivel(HttpMessageNotReadableException ex) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido.", List.of(
                "Envie um JSON válido. Valores aceitos: perfil (estudante, motorista, admin) "
                        + "e status (ativo, inativo, pendente)."));
    }

    /** 400: parâmetro de rota ou consulta com tipo incompatível. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tratarParametroInvalido(MethodArgumentTypeMismatchException ex) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Parâmetro inválido.",
                List.of("O valor informado para '" + ex.getName() + "' não é válido."));
    }

    /** 500: falha interna não prevista, preservando status HTTP de exceções padrão do Spring MVC. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroInesperado(Exception ex) {
        if (ex instanceof ErrorResponse errorResponse) {
            HttpStatus status = HttpStatus.resolve(errorResponse.getStatusCode().value());
            HttpStatus codigo = (status != null) ? status : HttpStatus.INTERNAL_SERVER_ERROR;
            return montarResposta(codigo, codigo.getReasonPhrase(), List.of());
        }

        log.error("Erro interno inesperado no processamento da requisição.", ex);
        return montarResposta(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno inesperado no servidor.",
                List.of());
    }

    private ResponseEntity<ErroResponse> montarResposta(HttpStatus status, String erro, List<String> detalhes) {
        return ResponseEntity.status(status).body(new ErroResponse(status.value(), erro, detalhes));
    }
}
