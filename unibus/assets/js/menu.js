// assets/js/menu.js
// Controla a abertura/fechamento do menu de navegação em telas estreitas

const botaoMenu = document.getElementById("botao-menu");
const listaMenu = document.querySelector("#menu .menu");

if (botaoMenu && listaMenu) {
  botaoMenu.addEventListener("click", () => {
    const aberto = listaMenu.classList.toggle("aberto");
    botaoMenu.setAttribute("aria-expanded", aberto ? "true" : "false");
  });

  // Fecha o menu ao clicar em um link (útil em navegação de uma página só)
  listaMenu.querySelectorAll("a").forEach((link) => {
    link.addEventListener("click", () => {
      listaMenu.classList.remove("aberto");
      botaoMenu.setAttribute("aria-expanded", "false");
    });
  });
}
