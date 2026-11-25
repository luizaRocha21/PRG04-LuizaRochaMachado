# UniBus

Plataforma web de transporte universitário voltada ao acompanhamento de rotas, consulta de horários e gerenciamento administrativo de usuários (estudantes, motoristas e administradores).

Projeto desenvolvido na disciplina **PRG04 - Programação Web**.

---

## Arquitetura e Tecnologias

O sistema é dividido em uma aplicação SPA (Single Page Application) no frontend e uma API RESTful no backend:

- **Frontend (`unibus/`)**: React 19, React Router DOM 7, Vite 8, Bootstrap 5 e CSS customizado com abordagem mobile-first. Hospedado na **Vercel**.
- **Backend (`backend/`)**: Java 21, Spring Boot 4.2 (Spring Web, Spring Data JPA, Bean Validation), empacotado em container Docker e hospedado no **Render**.
- **Banco de Dados**: PostgreSQL gerenciado no **Supabase** (ambiente de produção) e H2 Database em memória (desenvolvimento local e testes automatizados).

### API em Produção

- **Base URL**: [https://unibus-api-i31f.onrender.com](https://unibus-api-i31f.onrender.com)
- **Recurso de Usuários**: `https://unibus-api-i31f.onrender.com/api/usuarios`

---

## Estrutura do Repositório

```text
├── render.yaml                 # Especificação de deploy do backend no Render
├── backend/                    # API REST em Java + Spring Boot
│   ├── Dockerfile              # Build multi-stage da imagem Docker
│   ├── pom.xml                 # Dependências e configuração do Maven
│   ├── README.md               # Documentação técnica da API
│   └── src/
│       ├── main/
│       │   ├── java/com/unibus/backend/
│       │   │   ├── client/     # Consumo de API externa com WebClient (ViaCepClient)
│       │   │   ├── config/     # Configurações de CORS e WebClient
│       │   │   ├── controller/ # Endpoints REST (usuários, CEP e status)
│       │   │   ├── dto/        # Objetos de transferência de dados
│       │   │   ├── exception/  # Exceções de domínio e tratamento global HTTP
│       │   │   ├── mapper/     # Conversão entre entidades e DTOs (ObjectMapperUtil)
│       │   │   ├── model/      # Entidades JPA e enumerações
│       │   │   ├── repository/ # Repositórios Spring Data JPA
│       │   │   └── service/    # Regras de negócio e controle transacional
│       │   └── resources/      # Perfis de configuração (default, local e prod)
│       └── test/               # Testes unitários, de fatia e de integração
└── unibus/                     # Aplicação frontend em React + Vite
    ├── index.html              # Documento HTML principal
    ├── package.json            # Dependências e scripts do frontend
    ├── vite.config.js          # Configuração de build do Vite
    ├── vercel.json             # Regras de roteamento SPA na Vercel
    ├── .env.example            # Modelo de variáveis de ambiente do frontend
    ├── public/
    │   └── favicon.svg         # Ícone e logotipo da aplicação
    └── src/
        ├── main.jsx            # Inicialização do React e provedor de rotas
        ├── App.jsx             # Mapeamento das rotas da interface
        ├── assets/images/      # Ilustrações vetoriais das rotas por turno
        ├── components/         # Componentes reutilizáveis de interface
        ├── data/               # Dados estáticos de apoio da interface
        ├── hooks/              # Hooks customizados (useTitulo)
        ├── pages/              # Páginas da aplicação (Home, ComoFunciona, PaletaCores, Login, Admin, NotFound)
        └── styles/             # Folhas de estilo globais e modulares
```

---

## Rotas do Frontend

| Rota             | Página                | Descrição                                                                 |
| ---------------- | --------------------- | ------------------------------------------------------------------------- |
| `/`              | Início                | Apresentação da plataforma, destaques do serviço e navegação rápida       |
| `/como-funciona` | Como funciona         | Etapas do fluxo de embarque e galeria de rotas por turno                  |
| `/paleta-cores`  | Paleta de cores       | Documentação visual das cores e da tipografia aplicadas no sistema        |
| `/login`         | Login                 | Formulário de acesso com validação de campos                              |
| `/admin`         | Painel administrativo | Cadastro, listagem e exclusão de usuários integrados à API REST           |

---

## Execução Local

### 1. Backend (API REST)

Requisito: **JDK 21**. Por padrão, a aplicação executa sob o perfil `local`, utilizando banco H2 em memória sem necessidade de configuração externa.

```bash
cd backend
./mvnw spring-boot:run
```

A API ficará disponível em `http://localhost:8080`.

### 2. Frontend (React + Vite)

Requisito: **Node.js 20+**. As variáveis de ambiente utilizadas pelo frontend estão documentadas em `unibus/.env.example` (por padrão, `VITE_API_URL=http://localhost:8080/api/usuarios`).

```bash
cd unibus
npm install
npm run dev
```

Documentação detalhada sobre endpoints, contratos JSON, perfis de execução e variáveis de ambiente do servidor está disponível em [`backend/README.md`](backend/README.md).
