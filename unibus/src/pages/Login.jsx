import { useNavigate } from "react-router-dom";
import LoginForm from "../components/LoginForm";
import useTitulo from "../hooks/useTitulo";
import "../styles/login.css";

export default function Login() {
  useTitulo("Login");
  const navigate = useNavigate();

  return (
    <div className="container secao">
      <div className="login-wrap">
        <h1>Acesse sua conta</h1>
        <p>Autenticação fictícia, apenas para fins didáticos.</p>

        <div className="cartao">
          <LoginForm aoEntrar={() => navigate("/admin")} />
        </div>
      </div>
    </div>
  );
}
