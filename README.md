# UniBus

Aplicação web do **UniBus**, uma plataforma de transporte universitário com rastreio em tempo real, motoristas verificados e avisos instantâneos para estudantes.

Projeto acadêmico da disciplina **PRG04 - PW**. A interface foi construída em **React**, organizada em componentes reutilizáveis, com navegação entre páginas feita pelo **React Router DOM**. Versões anteriores do projeto eram em HTML, CSS e JavaScript puros.

## Páginas

| Rota             | Página             | Conteúdo                                                        |
| ---------------- | ------------------ | --------------------------------------------------------------- |
| `/`              | Início             | Apresentação, destaques do serviço e links para as demais páginas |
| `/como-funciona` | Como funciona      | Passo a passo do embarque e galeria de rotas por horário        |
| `/paleta-cores`  | Paleta de cores    | Paleta adotada no projeto e fontes utilizadas                   |
| `/sandbox`       | SandBox            | Página de testes com áudio e vídeo incorporados                 |
| `/login`         | Login              | Formulário com validação (autenticação fictícia)                |
| `/admin`         | Painel administrativo | Tabela de usuários cadastrados (dados fictícios)             |

Endereços inexistentes exibem uma página de "Página não encontrada".

## Tecnologias

- [React](https://react.dev) 19
- [React Router DOM](https://reactrouter.com) 7
- [Vite](https://vite.dev) 8
- [Bootstrap](https://getbootstrap.com) 5 (grid, cards, tabela e formulário)
- CSS próprio com variáveis e abordagem mobile-first
- Fontes Archivo e Inter (Google Fonts)

## Componentes

| Componente        | Descrição                                                         |
| ----------------- | ----------------------------------------------------------------- |
| `Layout`          | Estrutura comum às páginas: cabeçalho, conteúdo da rota e rodapé  |
| `Header`          | Cabeçalho com logotipo, título e botão do menu                    |
| `Navbar`          | Menu de navegação entre as páginas (abre e fecha no mobile)       |
| `Footer`          | Rodapé com informações de autoria                                 |
| `Hero`            | Seção de apresentação da página inicial                           |
| `FeatureCard`     | Card de destaque da página inicial                                |
| `PageLinks`       | Lista de links "Explore o projeto"                                |
| `StepList`        | Lista com o passo a passo do embarque                             |
| `RouteGallery`    | Galeria que troca imagem e legenda da rota ao clicar no botão     |
| `ColorSwatch`     | Amostra de uma cor da paleta                                      |
| `FontTable`       | Tabela com as fontes do projeto                                   |
| `MediaBlock`      | Bloco com título e descrição para mídia (áudio e vídeo)           |
| `LoginForm`       | Formulário de autenticação com validação dos campos               |
| `UserTable`       | Tabela de usuários do painel administrativo                       |
| `StatusBadge`     | Selo colorido de acordo com o status do usuário                   |
| `ScrollToTop`     | Retorna ao topo da página a cada troca de rota                    |

O menu, a galeria de rotas e o formulário de login usam estado (`useState`) e tratamento de eventos; mensagens de erro, estado do menu e cor dos selos são exibidos de forma condicional.

## Estrutura do projeto

```
unibus/
├── index.html              → documento base (fontes e ponto de montagem do React)
├── package.json            → dependências e scripts
├── vite.config.js          → configuração do Vite
├── vercel.json             → redireciona todas as rotas para index.html (Vercel)
├── public/
│   ├── favicon.svg         → ícone da aba e logotipo do cabeçalho
│   └── _redirects          → mesma regra de redirecionamento para o Netlify
└── src/
    ├── main.jsx            → ponto de entrada; ativa o React Router
    ├── App.jsx             → definição das rotas
    ├── components/         → componentes reutilizáveis
    ├── pages/              → uma página por rota
    ├── data/               → dados exibidos nas páginas (menu, rotas, cores, usuários…)
    ├── hooks/
    │   └── useTitulo.js    → define o título da aba para cada página
    ├── styles/
    │   ├── global.css      → paleta, tipografia, cabeçalho/menu e responsividade
    │   ├── como-funciona.css
    │   ├── paleta.css
    │   ├── sandbox.css
    │   └── login.css
    └── assets/
        └── images/         → ilustrações das rotas (manhã, tarde e noite)
```
