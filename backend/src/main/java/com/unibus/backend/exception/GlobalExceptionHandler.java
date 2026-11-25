package com.unibus.backend.exception;

import com.unibus.backend.dto.ErroResponse;
import java.util.List;
import java.util.Objects;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Traduz as exceções da aplicação em respostas HTTP com o status code correto e um corpo
 * JSON padronizado ({@link ErroResponse}).
 *
 * <p>Os controllers e o serviço apenas lançam exceções; toda a conversão para HTTP fica aqui.
 * Erros não previstos (500) e erros de roteamento (404 de rota inexistente, 405, 415) continuam
 * sendo tratados pelo próprio Spring, que já devolve o status adequado.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 404: o id informado não existe. */
    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarUsuarioNaoEncontrado(UsuarioNaoEncontradoException ex) {
        return montarResposta(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
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

    /** 400: algum campo do corpo falhou na validação (@Valid). Devolve uma mensagem por campo. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)
                .distinct()
                .sorted() // ordem estável, já que o Bean Validation não garante a ordem das violações
                .toList();
        return montarResposta(HttpStatus.BAD_REQUEST, "Dados inválidos.", detalhes);
    }

    /** 400: JSON malformado ou com valor inválido (ex.: perfil que não existe). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> tratarCorpoIlegivel(HttpMessageNotReadableException ex) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Corpo da requisição inválido.", List.of(
                "Envie um JSON válido. Valores aceitos: perfil (estudante, motorista, admin) "
                        + "e status (ativo, inativo, pendente)."));
    }

    /** 400: um parâmetro da URL tem tipo errado (ex.: GET /api/usuarios/abc, quando o id é numérico). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tratarParametroInvalido(MethodArgumentTypeMismatchException ex) {
        return montarResposta(HttpStatus.BAD_REQUEST, "Parâmetro inválido.",
                List.of("O valor informado para '" + ex.getName() + "' não é válido."));
    }

    private ResponseEntity<ErroResponse> montarResposta(HttpStatus status, String erro, List<String> detalhes) {
        return ResponseEntity.status(status).body(new ErroResponse(status.value(), erro, detalhes));
    }
}
