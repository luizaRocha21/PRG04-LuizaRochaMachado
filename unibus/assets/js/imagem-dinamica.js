// assets/js/imagem-dinamica.js
// Responsável por trocar a imagem e a legenda da seção "Frota em diferentes
// horários" da página atividade-3.html.

const rotas = [
  {
    src: "../assets/images/rota-manha.svg",
    alt: "Ilustração do ônibus universitário na rota da manhã",
    legenda: "Rota 1 — Saída 07:00",
  },
  {
    src: "../assets/images/rota-tarde.svg",
    alt: "Ilustração do ônibus universitário na rota da tarde",
    legenda: "Rota 2 — Saída 13:00",
  },
  {
    src: "../assets/images/rota-noite.svg",
    alt: "Ilustração do ônibus universitário na rota da noite",
    legenda: "Rota 3 — Saída 21:30",
  },
];

let indiceAtual = 0;

const imagemRota = document.getElementById("imagem-rota");
const legendaRota = document.getElementById("legenda-rota");
const botaoTrocarImagem = document.getElementById("botao-trocar-imagem");

function trocarImagem() {
  indiceAtual = (indiceAtual + 1) % rotas.length;
  const rota = rotas[indiceAtual];

  imagemRota.src = rota.src;
  imagemRota.alt = rota.alt;
  legendaRota.textContent = rota.legenda;
}

if (botaoTrocarImagem) {
  botaoTrocarImagem.addEventListener("click", trocarImagem);
}
