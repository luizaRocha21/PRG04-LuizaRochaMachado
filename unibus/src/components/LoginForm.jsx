import { useState } from "react";

const REGEX_EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

// Retorna as mensagens de erro por campo; objeto vazio quando tudo é válido
function validar({ email, senha }) {
  const erros = {};
  const emailLimpo = email.trim();
  const senhaLimpa = senha.trim();

  if (!emailLimpo) {
    erros.email = "Informe seu e-mail.";
  } else if (!REGEX_EMAIL.test(emailLimpo)) {
    erros.email = "Digite um e-mail em um formato válido.";
  }

  if (!senhaLimpa) {
    erros.senha = "Informe sua senha.";
  } else if (senhaLimpa.length < 4) {
    erros.senha = "A senha deve ter pelo menos 4 caracteres.";
  }

  return erros;
}

const classeCampo = (erro) => (erro ? "form-control is-invalid" : "form-control");

export default function LoginForm({ aoEntrar }) {
  const [campos, setCampos] = useState({ email: "", senha: "" });
  const [erros, setErros] = useState({});

  function atualizarCampo(evento) {
    const { name, value } = evento.target;
    setCampos((atuais) => ({ ...atuais, [name]: value }));
  }

  function enviar(evento) {
    evento.preventDefault();

    const novosErros = validar(campos);
    setErros(novosErros);

    if (Object.keys(novosErros).length === 0) {
      aoEntrar();
    }
  }

  return (
    <form onSubmit={enviar} noValidate>
      <div className="mb-3">
        <label htmlFor="email" className="form-label">
          E-mail
        </label>
        <input
          type="email"
          id="email"
          name="email"
          className={classeCampo(erros.email)}
          placeholder="voce@exemplo.com"
          autoComplete="email"
          value={campos.email}
          onChange={atualizarCampo}
          aria-invalid={Boolean(erros.email)}
        />
        {erros.email && <div className="invalid-feedback">{erros.email}</div>}
      </div>

      <div className="mb-3">
        <label htmlFor="senha" className="form-label">
          Senha
        </label>
        <input
          type="password"
          id="senha"
          name="senha"
          className={classeCampo(erros.senha)}
          placeholder="••••••••"
          autoComplete="current-password"
          value={campos.senha}
          onChange={atualizarCampo}
          aria-invalid={Boolean(erros.senha)}
        />
        {erros.senha && <div className="invalid-feedback">{erros.senha}</div>}
      </div>

      <button type="submit" className="btn btn-warning w-100 fw-bold">
        Entrar
      </button>
    </form>
  );
}
