# UniBus — Backend (API REST)

API RESTful responsável pelo gerenciamento de usuários da plataforma UniBus (estudantes, motoristas e administradores), desenvolvida com **Java 21**, **Spring Boot 4.2**, **Spring Data JPA** e **PostgreSQL (Supabase)**, com deploy em container Docker no **Render**.

- **URL de Produção**: `https://unibus-api-i31f.onrender.com`

---

## Arquitetura em Camadas

O código-fonte (`src/main/java/com/unibus/backend/`) está organizado nas seguintes camadas:

- **`controller`**: exposição dos endpoints HTTP (`UsuarioController`, `CepController` e `StatusController`), mapeamento de rotas, paginação, verbos e códigos de status.
- **`service`**: regras de negócio, listagem paginada, validação de unicidade de e-mail e controle transacional (`UsuarioService`).
- **`repository`**: interface de persistência com paginação nativa (`Pageable`) e consultas derivadas via Spring Data JPA (`UsuarioRepository`).
- **`model`**: entidade de domínio mapeada para o banco relacional (`Usuario`) e enumerações (`Perfil`, `StatusUsuario`).
- **`dto`**: contratos de entrada e saída em JSON (`UsuarioRequest`, `UsuarioResponse`, `CepResponse`, `ErroResponse`, `StatusResponse`), mantendo a entidade JPA isolada da camada web.
- **`mapper`**: conversão automatizada entre entidades JPA e DTOs utilizando `ObjectMapper` do Jackson (`ObjectMapperUtil`).
- **`client`**: consumo reativo da API externa ViaCEP com `WebClient` (`ViaCepClient`).
- **`exception`**: exceções de domínio (`UsuarioNaoEncontradoException`, `EmailJaCadastradoException`, `CepInvalidoException`, `CepNaoEncontradoException`) e tratamento centralizado com `@RestControllerAdvice` (`GlobalExceptionHandler`).
- **`config`**: política de CORS (`CorsConfig`) e configuração do bean `WebClient` (`WebClientConfig`).

---

## Endpoints

| Método HTTP | Rota                 | Descrição                                                                 | Respostas de Sucesso               | Respostas de Erro        |
| ----------- | -------------------- | ------------------------------------------------------------------------- | ---------------------------------- | ------------------------ |
| `GET`       | `/`                  | Verificação de disponibilidade (health check)                             | `200 OK`                           | —                        |
| `GET`       | `/api/usuarios`      | Lista usuários de forma paginada (`?page=0&size=10&sort=id,asc`)          | `200 OK` (`Page<UsuarioResponse>`) | —                        |
| `GET`       | `/api/usuarios/{id}` | Busca um usuário pelo identificador                                       | `200 OK`                           | `400`, `404`             |
| `POST`      | `/api/usuarios`      | Cadastra um novo usuário                                                  | `201 Created` (header `Location`)  | `400`, `409`, `415`      |
| `PUT`       | `/api/usuarios/{id}` | Atualiza integralmente um usuário existente                               | `200 OK`                           | `400`, `404`, `409`      |
| `DELETE`    | `/api/usuarios/{id}` | Remove um usuário pelo identificador                                      | `204 No Content`                   | `400`, `404`             |
| `GET`       | `/api/cep/{cep}`     | Consulta dados de endereço por CEP via integração `WebClient` (ViaCEP)    | `200 OK` (`CepResponse`)           | `400`, `404`, `502`      |

### Parâmetros de Paginação (`GET /api/usuarios`)

- `page`: índice da página baseado em zero (padrão: `0`).
- `size`: quantidade de registros por página (padrão: `10`).
- `sort`: campo e direção de ordenação (padrão: `id,asc`).

### Contrato JSON (`POST` e `PUT`)

```json
{
  "nome": "Ana Souza",
  "email": "ana.souza@unibus.com",
  "perfil": "estudante",
  "status": "ativo"
}
```

- `nome`: obrigatório, entre 2 e 100 caracteres (normalizado com `trim`).
- `email`: obrigatório, formato de e-mail válido, até 254 caracteres, único (normalizado em minúsculas).
- `perfil`: obrigatório (`estudante`, `motorista` ou `admin`).
- `status`: obrigatório (`ativo`, `inativo` ou `pendente`).

---

## Perfis de Execução e Variáveis de Ambiente

A aplicação possui dois perfis principais de execução em `src/main/resources/`:

- **`local` (`application-local.properties`)**: perfil ativo por padrão fora do container. Utiliza banco **H2 em memória** (`jdbc:h2:mem:unibus`) com criação automática do esquema (`create-drop`) e console habilitado em `/h2-console`.
- **`prod` (`application-prod.properties`)**: perfil ativado no `Dockerfile` (`SPRING_PROFILES_ACTIVE=prod`). Conecta-se ao **PostgreSQL (Supabase)** via HikariCP e desabilita o console H2.

### Variáveis de Ambiente

| Variável               | Obrigatória (Prod) | Valor Padrão                                  | Descrição                                                        |
| ---------------------- | ------------------ | --------------------------------------------- | ---------------------------------------------------------------- |
| `DB_URL`               | Sim                | —                                             | URL JDBC de conexão com o PostgreSQL (Supabase Session Pooler)   |
| `DB_USERNAME`          | Sim                | —                                             | Usuário do banco de dados (`postgres.<project-ref>`)             |
| `DB_PASSWORD`          | Sim                | —                                             | Senha do banco de dados                                          |
| `CORS_ALLOWED_ORIGINS` | Sim                | `http://localhost:3000,http://localhost:5173` | Lista de origens permitidas no CORS (separadas por vírgula)      |
| `DB_POOL_SIZE`         | Não                | `5`                                           | Limite máximo de conexões simultâneas no pool HikariCP           |
| `DDL_AUTO`             | Não                | `update`                                      | Estratégia de DDL do Hibernate (`update` ou `validate`)          |
| `PORT`                 | Não                | `8080`                                        | Porta HTTP na qual o servidor embutido escuta                    |

---

## Testes Automatizados

A suíte de testes cobre testes unitários de serviço (`Mockito`), testes da camada web (`@WebMvcTest`), testes de persistência (`@DataJpaTest`) e testes de integração ponta a ponta (`@SpringBootTest` + `MockMvc` com perfil `test`).

Para executar todos os testes automatizados:

```bash
cd backend
./mvnw test
```
