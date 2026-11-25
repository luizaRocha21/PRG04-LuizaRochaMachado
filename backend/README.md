# UniBus — Backend (API REST)

API REST do UniBus para gerenciar usuários (estudantes, motoristas e administradores).
Construída com **Spring Boot**, **Spring Data JPA** e **PostgreSQL (Supabase)**, publicada no **Render**.

## Sumário

1. [Tecnologias](#tecnologias)
2. [Estrutura de pacotes (camadas)](#estrutura-de-pacotes-camadas)
3. [Endpoints](#endpoints)
4. [Como rodar localmente](#como-rodar-localmente)
5. [Testes](#testes)
6. [Banco de dados no Supabase](#banco-de-dados-no-supabase)
7. [Deploy no Render](#deploy-no-render)
8. [Conectando o frontend](#conectando-o-frontend)
9. [Variáveis de ambiente](#variáveis-de-ambiente)
10. [O que mudou em relação ao backend anterior](#o-que-mudou-em-relação-ao-backend-anterior)

## Tecnologias

| Item | Uso |
|------|-----|
| Java 21 + Spring Boot 4.2 | base da aplicação |
| Spring Web (MVC) | endpoints REST e JSON |
| Spring Data JPA (Hibernate) | persistência e repositórios |
| Bean Validation | validação dos dados de entrada (`@Valid`) |
| PostgreSQL (Supabase) | banco de dados em produção |
| H2 | banco em memória para rodar localmente e nos testes |
| JUnit 5, Mockito, MockMvc | testes |
| Maven (wrapper `mvnw`) | build |
| Docker | imagem usada no Render |

## Estrutura de pacotes (camadas)

```
src/main/java/com/unibus/backend/
├── BackendApplication.java      → ponto de entrada
├── controller/                  → camada web: rotas, verbos HTTP e status codes
│   ├── UsuarioController.java
│   └── StatusController.java    → GET / (health check)
├── service/                     → camada de negócio: regras e transações
│   └── UsuarioService.java
├── repository/                  → camada de dados: Spring Data JPA
│   └── UsuarioRepository.java
├── model/                       → entidade JPA e enums
│   ├── Usuario.java
│   ├── Perfil.java
│   └── StatusUsuario.java
├── dto/                         → objetos de entrada e saída da API
│   ├── UsuarioRequest.java      → corpo do POST/PUT (com validações)
│   ├── UsuarioResponse.java
│   ├── ErroResponse.java
│   └── StatusResponse.java
├── exception/                   → exceções de negócio e tradução para HTTP
│   ├── UsuarioNaoEncontradoException.java
│   ├── EmailJaCadastradoException.java
│   └── GlobalExceptionHandler.java
└── config/
    └── CorsConfig.java          → libera o frontend (CORS)
```

Fluxo de uma requisição:

```
Cliente ──HTTP──▶ Controller ──▶ Service ──▶ Repository ──▶ Banco
                      │             │
                      └── DTOs ◀────┘   (a entidade JPA nunca sai da API)
```

## Endpoints

Base: `/api/usuarios`

| Verbo | Rota | Descrição | Sucesso | Erros |
|-------|------|-----------|---------|-------|
| `GET` | `/api/usuarios` | lista todos (ordenados por id) | `200 OK` | — |
| `GET` | `/api/usuarios/{id}` | busca um usuário | `200 OK` | `400` (id não numérico), `404` |
| `POST` | `/api/usuarios` | cadastra um usuário | `201 Created` + cabeçalho `Location` | `400`, `409` |
| `PUT` | `/api/usuarios/{id}` | substitui **todos** os dados | `200 OK` | `400`, `404`, `409` |
| `DELETE` | `/api/usuarios/{id}` | remove o usuário | `204 No Content` | `404` |
| `GET` | `/` | verificação de saúde da API | `200 OK` | — |

Outros verbos nessas rotas (por exemplo `PATCH`) retornam `405 Method Not Allowed`, e um `Content-Type`
que não seja JSON retorna `415 Unsupported Media Type`.

### Corpo do POST e do PUT

```json
{
  "nome": "Ana Souza",
  "email": "ana.souza@unibus.com",
  "perfil": "estudante",
  "status": "ativo"
}
```

| Campo | Regra |
|-------|-------|
| `nome` | obrigatório, de 2 a 100 caracteres (espaços nas pontas são removidos) |
| `email` | obrigatório, formato válido, até 254 caracteres, **único** (gravado em minúsculas) |
| `perfil` | obrigatório: `estudante`, `motorista` ou `admin` |
| `status` | obrigatório: `ativo`, `inativo` ou `pendente` |

Resposta de sucesso (`201` no POST, `200` no PUT e no GET):

```json
{
  "id": 1,
  "nome": "Ana Souza",
  "email": "ana.souza@unibus.com",
  "perfil": "estudante",
  "status": "ativo"
}
```

### Respostas de erro

Todas seguem o mesmo formato:

```json
{
  "status": 400,
  "erro": "Dados inválidos.",
  "detalhes": [
    "O campo 'email' é obrigatório e deve ter um formato válido (até 254 caracteres).",
    "O campo 'nome' é obrigatório e deve ter entre 2 e 100 caracteres."
  ]
}
```

| Status | Quando acontece |
|--------|-----------------|
| `400 Bad Request` | campo inválido, JSON malformado, valor de `perfil`/`status` desconhecido ou id não numérico |
| `404 Not Found` | não existe usuário com o id informado |
| `409 Conflict` | o e-mail já pertence a outro usuário |

### Exemplos com `curl`

```bash
# cadastrar
curl -i -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome":"Ana Souza","email":"ana@unibus.com","perfil":"estudante","status":"ativo"}'

# listar / buscar
curl http://localhost:8080/api/usuarios
curl http://localhost:8080/api/usuarios/1

# substituir todos os dados
curl -X PUT http://localhost:8080/api/usuarios/1 \
  -H "Content-Type: application/json" \
  -d '{"nome":"Ana Lima","email":"ana@unibus.com","perfil":"admin","status":"ativo"}'

# excluir
curl -i -X DELETE http://localhost:8080/api/usuarios/1
```

## Como rodar localmente

Requisito: **JDK 21**. Não é preciso instalar banco nem configurar nada: o perfil padrão (`local`)
usa H2 em memória (os dados somem ao encerrar).

```bash
cd backend
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

- API: <http://localhost:8080>
- Console do H2: <http://localhost:8080/h2-console> (JDBC URL `jdbc:h2:mem:unibus`, usuário `sa`, senha em branco)

## Testes

```bash
cd backend
./mvnw test
```

| Classe de teste | Tipo | O que cobre |
|-----------------|------|-------------|
| `UsuarioControllerTest` | `@WebMvcTest` (camada web, serviço simulado) | **todos os métodos do `UsuarioController`**: listar, buscarPorId, criar, atualizar e remover — sucesso e erros (400, 404, 409, 405, 415), `Location`, formato do JSON e normalização dos dados |
| `StatusControllerTest` | `@WebMvcTest` | `GET /` |
| `UsuarioServiceTest` | unitário (Mockito) | regras de negócio: id inexistente, e-mail único, substituição dos dados |
| `UsuarioRepositoryTest` | `@DataJpaTest` (H2) | mapeamento JPA, consultas derivadas e restrição de e-mail único no banco |
| `UsuarioApiIntegrationTest` | `@SpringBootTest` + MockMvc (H2) | fluxo completo da API sem mocks e configuração de CORS |
| `BackendApplicationTests` | `@SpringBootTest` | o contexto da aplicação sobe |

## Banco de dados no Supabase

1. Crie um projeto em <https://supabase.com> e **guarde a senha do banco** definida na criação.
2. No painel do projeto, clique em **Connect** e abra a opção **Session pooler**.
   Copie dali o *host* (algo como `aws-0-sa-east-1.pooler.supabase.com`) e o *user*
   (`postgres.<project-ref>`).

   > Não use a **Direct connection**: no plano gratuito ela só aceita IPv6 e o Render só sai por IPv4,
   > então a conexão falharia. O Session pooler funciona em IPv4.

3. Monte as três variáveis que o backend usa (elas serão cadastradas no Render):

   | Variável | Valor |
   |----------|-------|
   | `DB_URL` | `jdbc:postgresql://<host-do-pooler>:5432/postgres?sslmode=require` |
   | `DB_USERNAME` | `postgres.<project-ref>` |
   | `DB_PASSWORD` | a senha do banco |

   A senha vai em uma variável própria (`DB_PASSWORD`), então caracteres especiais nela **não** precisam ser
   codificados como acontece em *connection strings* `postgresql://...`.

4. A tabela `usuarios` é criada automaticamente na primeira inicialização (`DDL_AUTO=update`).
5. **Recomendado:** depois da primeira inicialização, abra o **SQL Editor** do Supabase e execute:

   ```sql
   alter table public.usuarios enable row level security;
   ```

   O Supabase expõe as tabelas do schema `public` por uma API própria; com isso, ninguém consegue ler
   ou alterar os dados por esse caminho. O backend não é afetado, pois conecta diretamente com o usuário
   `postgres`, que ignora o RLS.

> Se preferir o **Transaction pooler** (porta `6543`), acrescente `&prepareThreshold=0` ao final da `DB_URL`.

## Deploy no Render

O Render não executa Java diretamente: ele constrói a imagem a partir do [`Dockerfile`](Dockerfile) desta pasta.

### Opção A — Blueprint (recomendada)

O arquivo [`render.yaml`](../render.yaml), na raiz do repositório, já descreve o serviço.

1. Faça *push* do projeto para o GitHub.
2. No Render: **New → Blueprint**, selecione o repositório e confirme.
3. Preencha as variáveis pedidas: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `CORS_ALLOWED_ORIGINS`.

### Opção B — Web Service manual

**New → Web Service**, escolha o repositório e configure:

| Campo | Valor |
|-------|-------|
| Language | `Docker` |
| Root Directory | *(deixe em branco)* |
| Dockerfile Path | `backend/Dockerfile` |
| Docker Build Context Directory | `backend` |
| Health Check Path | `/` |
| Instance Type | `Free` |

Em **Environment**, cadastre `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` e `CORS_ALLOWED_ORIGINS`.

### Conferindo

Quando o deploy terminar, abra `https://<seu-servico>.onrender.com/`. A resposta esperada é:

```json
{"status":"online","servico":"UniBus API"}
```

e `https://<seu-servico>.onrender.com/api/usuarios` deve retornar `[]`.

> **Plano gratuito:** o Render hiberna o serviço após cerca de 15 minutos sem requisições, e a primeira
> requisição depois disso pode levar cerca de um minuto (o Spring Boot precisa iniciar). Projetos gratuitos
> do Supabase também podem ser pausados por inatividade; se isso ocorrer, reative-o pelo painel.

## Conectando o frontend

1. **Vercel** (Settings → Environment Variables):
   `VITE_API_URL = https://<seu-servico>.onrender.com/api/usuarios` e faça um novo deploy
   (o Vite embute a variável no momento do build).
2. **Render**: em `CORS_ALLOWED_ORIGINS` informe o endereço do frontend, por exemplo
   `https://seu-app.vercel.app`. Para mais de um, separe por vírgula; padrões também funcionam
   (`https://*.vercel.app` libera as *preview deployments*).
3. **Desenvolvimento local**: copie `unibus/.env.example` para `unibus/.env.local`
   (por padrão o frontend já aponta para `http://localhost:8080/api/usuarios`).

## Variáveis de ambiente

| Variável | Obrigatória | Padrão | Descrição |
|----------|-------------|--------|-----------|
| `DB_URL` | em produção | — | URL JDBC do PostgreSQL (Supabase) |
| `DB_USERNAME` | em produção | — | usuário do banco (`postgres.<project-ref>`) |
| `DB_PASSWORD` | em produção | — | senha do banco |
| `CORS_ALLOWED_ORIGINS` | recomendada | `http://localhost:3000,http://localhost:5173` | origens do frontend autorizadas |
| `DB_POOL_SIZE` | não | `5` | conexões simultâneas com o banco |
| `DDL_AUTO` | não | `update` | `update` cria/ajusta a tabela; `validate` apenas confere |
| `JAVA_OPTS` | não | ver `Dockerfile` | opções da JVM |
| `PORT` | definida pelo Render | `8080` | porta HTTP |
| `SPRING_PROFILES_ACTIVE` | definida no `Dockerfile` | `local` fora do Docker | `prod` no Render |

Nenhum segredo é versionado: senhas e URLs ficam apenas nas variáveis de ambiente do Render.

## O que mudou em relação ao backend anterior

O backend em Node.js/Express/Firebase foi totalmente substituído. O frontend continua usando os mesmos
endereços e o mesmo formato de JSON; as diferenças são estas:

| Antes (Express + Firestore) | Agora (Spring Boot + PostgreSQL) |
|-----------------------------|----------------------------------|
| `id` era texto gerado pelo Firestore | `id` é numérico, gerado pelo banco |
| `DELETE` respondia `200` com uma mensagem | `DELETE` responde `204 No Content` |
| `POST` respondia `201` sem `Location` | `POST` responde `201` com o cabeçalho `Location` |
| `PUT` mesclava apenas os campos enviados | `PUT` substitui o recurso inteiro (todos os campos são obrigatórios); veja a seção de endpoints |
| `perfil` e `status` tinham valor padrão | `perfil` e `status` são obrigatórios (o frontend já os envia) |
| e-mails podiam se repetir | e-mail **único** (`409 Conflict` ao repetir) |
| erro: `{ "erro", "detalhes" }` | erro: `{ "status", "erro", "detalhes" }` |

Os dados que estavam no Firestore **não** são migrados automaticamente. A chave
`serviceAccountKey.json` do Firebase não é mais necessária: exclua-a da sua máquina e revogue-a em
*Console do Firebase → Configurações do projeto → Contas de serviço*.

## Limitações conhecidas

- **Sem autenticação:** assim como a versão anterior, a API é aberta; quem souber a URL consegue listar,
  criar, alterar e excluir usuários. É adequado para o contexto acadêmico; para uso real, adicione
  Spring Security (por exemplo, JWT) e proteja as rotas.
- **Sem paginação:** `GET /api/usuarios` devolve todos os registros, o que atende ao frontend atual.
