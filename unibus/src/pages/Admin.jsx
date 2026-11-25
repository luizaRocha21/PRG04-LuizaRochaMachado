import { useEffect, useState } from "react";
import UserTable from "../components/UserTable";
import useTitulo from "../hooks/useTitulo";

const API_URL = import.meta.env.VITE_API_URL || "http://localhost:8080/api/usuarios";

export default function Admin() {
  useTitulo("Painel Administrativo");

  const [usuarios, setUsuarios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [erro, setErro] = useState("");

  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [perfil, setPerfil] = useState("estudante");
  const [status, setStatus] = useState("ativo");

  useEffect(() => {
    async function carregarUsuarios() {
      try {
        setLoading(true);
        setErro("");
        const resposta = await fetch(API_URL);
        if (!resposta.ok) {
          throw new Error("Erro ao buscar usuários da API.");
        }
        const dados = await resposta.json();
        setUsuarios(dados);
      } catch {
        setErro("Não foi possível carregar os usuários. Verifique se a API está em execução.");
      } finally {
        setLoading(false);
      }
    }

    carregarUsuarios();
  }, []);

  async function handleCadastrar(e) {
    e.preventDefault();
    try {
      setErro("");
      const resposta = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ nome, email, perfil, status }),
      });

      if (!resposta.ok) {
        throw new Error("Erro ao cadastrar usuário.");
      }

      const usuarioCriado = await resposta.json();
      setUsuarios((listaAtual) => [...listaAtual, usuarioCriado]);
      setNome("");
      setEmail("");
      setPerfil("estudante");
      setStatus("ativo");
    } catch {
      setErro("Não foi possível cadastrar o usuário. Verifique os dados ou a conexão com a API.");
    }
  }

  async function handleExcluir(id) {
    try {
      setErro("");
      const resposta = await fetch(`${API_URL}/${id}`, {
        method: "DELETE",
      });

      if (!resposta.ok) {
        throw new Error("Erro ao excluir usuário.");
      }

      setUsuarios((listaAtual) => listaAtual.filter((u) => u.id !== id));
    } catch {
      setErro("Não foi possível excluir o usuário.");
    }
  }

  return (
    <div className="container secao">
      <h1>Painel administrativo</h1>
      <p>Gerenciamento de usuários integrados à API.</p>

      {erro && (
        <div className="alert alert-danger" role="alert">
          {erro}
        </div>
      )}

      <form onSubmit={handleCadastrar} className="cartao p-3 mb-4">
        <div className="row g-2 align-items-end">
          <div className="col-md-3">
            <label className="form-label mb-1">Nome</label>
            <input
              type="text"
              className="form-control"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              required
            />
          </div>
          <div className="col-md-3">
            <label className="form-label mb-1">E-mail</label>
            <input
              type="email"
              className="form-control"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>
          <div className="col-md-2">
            <label className="form-label mb-1">Perfil</label>
            <select
              className="form-select"
              value={perfil}
              onChange={(e) => setPerfil(e.target.value)}
            >
              <option value="estudante">Estudante</option>
              <option value="motorista">Motorista</option>
              <option value="admin">Admin</option>
            </select>
          </div>
          <div className="col-md-2">
            <label className="form-label mb-1">Status</label>
            <select
              className="form-select"
              value={status}
              onChange={(e) => setStatus(e.target.value)}
            >
              <option value="ativo">Ativo</option>
              <option value="inativo">Inativo</option>
              <option value="pendente">Pendente</option>
            </select>
          </div>
          <div className="col-md-2 d-grid">
            <button type="submit" className="btn btn-primary">
              Cadastrar
            </button>
          </div>
        </div>
      </form>

      {loading ? (
        <p className="text-muted">Carregando usuários...</p>
      ) : (
        <UserTable usuarios={usuarios} onDelete={handleExcluir} />
      )}
    </div>
  );
}

