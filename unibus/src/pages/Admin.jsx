import UserTable from "../components/UserTable";
import { usuarios } from "../data/usuarios";
import useTitulo from "../hooks/useTitulo";

export default function Admin() {
  useTitulo("Painel Administrativo");

  return (
    <div className="container secao">
      <h1>Painel administrativo</h1>
      <p>Usuários cadastrados no sistema (dados fictícios).</p>

      <UserTable usuarios={usuarios} />
    </div>
  );
}
