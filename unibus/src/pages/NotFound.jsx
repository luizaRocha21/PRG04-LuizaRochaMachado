import { Link } from "react-router-dom";
import useTitulo from "../hooks/useTitulo";

export default function NotFound() {
  useTitulo("Página não encontrada");

  return (
    <div className="container secao">
      <h1>Página não encontrada</h1>
      <p>O endereço acessado não existe no UniBus.</p>
      <Link to="/" className="botao">
        Voltar ao início
      </Link>
    </div>
  );
}
