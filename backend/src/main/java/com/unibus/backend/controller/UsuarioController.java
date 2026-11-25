package com.unibus.backend.controller;

import com.unibus.backend.dto.UsuarioRequest;
import com.unibus.backend.dto.UsuarioResponse;
import com.unibus.backend.service.UsuarioService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Endpoints REST do recurso "usuários" ({@code /api/usuarios}).
 *
 * <pre>
 * Verbo   Rota                 Sucesso          Erros
 * GET     /api/usuarios        200 OK           -
 * GET     /api/usuarios/{id}   200 OK           404
 * POST    /api/usuarios        201 Created      400, 409
 * PUT     /api/usuarios/{id}   200 OK           400, 404, 409
 * DELETE  /api/usuarios/{id}   204 No Content   404
 * </pre>
 *
 * <p>O controller só cuida do protocolo HTTP (rotas, verbos, status e cabeçalhos). As regras de
 * negócio ficam no {@link UsuarioService}; as exceções viram respostas de erro no
 * {@code GlobalExceptionHandler}.</p>
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** GET /api/usuarios: lista todos os usuários (200 OK). */
    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listarTodos();
    }

    /** GET /api/usuarios/{id}: busca um usuário (200 OK, ou 404 se não existir). */
    @GetMapping("/{id}")
    public UsuarioResponse buscarPorId(@PathVariable("id") Long id) {
        return usuarioService.buscarPorId(id);
    }

    /**
     * POST /api/usuarios: cadastra um usuário.
     * Responde 201 Created com o recurso criado no corpo e a sua URL no cabeçalho Location.
     */
    @PostMapping
    public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse criado = usuarioService.criar(request);

        // Monta a URL do novo recurso a partir da requisição atual: .../api/usuarios/{id}
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criado.id())
                .toUri();

        return ResponseEntity.created(location).body(criado);
    }

    /**
     * PUT /api/usuarios/{id}: substitui TODOS os dados do usuário (200 OK).
     * Responde 404 se o id não existir. Por ser uma substituição completa, todos os campos são
     * obrigatórios no corpo.
     */
    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable("id") Long id,
                                     @Valid @RequestBody UsuarioRequest request) {
        return usuarioService.atualizar(id, request);
    }

    /** DELETE /api/usuarios/{id}: remove o usuário. Responde 204 No Content (sem corpo). */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable("id") Long id) {
        usuarioService.remover(id);
    }
}
