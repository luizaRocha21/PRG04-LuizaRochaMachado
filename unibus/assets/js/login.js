// assets/js/login.js
// Valida o formulário de login no cliente e simula a autenticação.

const form = document.getElementById("form-login");
const campoEmail = document.getElementById("email");
const campoSenha = document.getElementById("senha");

function marcarErro(campo, mensagem) {
  campo.classList.add("is-invalid");
  const feedback = campo.parentElement.querySelector(".invalid-feedback");
  if (feedback) feedback.textContent = mensagem;
}

function limparErro(campo) {
  campo.classList.remove("is-invalid");
}

form.addEventListener("submit", (evento) => {
  evento.preventDefault();
  let valido = true;

  limparErro(campoEmail);
  limparErro(campoSenha);

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  if (!campoEmail.value.trim()) {
    marcarErro(campoEmail, "Informe seu e-mail.");
    valido = false;
  } else if (!emailRegex.test(campoEmail.value.trim())) {
    marcarErro(campoEmail, "Digite um e-mail em um formato válido.");
    valido = false;
  }

  if (!campoSenha.value.trim()) {
    marcarErro(campoSenha, "Informe sua senha.");
    valido = false;
  } else if (campoSenha.value.trim().length < 4) {
    marcarErro(campoSenha, "A senha deve ter pelo menos 4 caracteres.");
    valido = false;
  }

  if (valido) {
    window.location.href = "admin.html";
  }
});
