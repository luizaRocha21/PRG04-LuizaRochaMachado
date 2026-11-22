import { Link, NavLink } from "react-router-dom";
import { itensMenu } from "../data/menu";

// NavLink marca o link da rota atual com aria-current="page"
export default function Navbar({ aberto, aoNavegar }) {
  return (
    <nav id="menu" aria-label="Navegação principal">
      <ul className={aberto ? "menu aberto" : "menu"}>
        {itensMenu.map((item) => (
          <li key={item.para}>
            <NavLink to={item.para} end={item.para === "/"} onClick={aoNavegar}>
              {item.rotulo}
            </NavLink>
          </li>
        ))}
        <li className="item-login-mobile">
          <Link to="/login" onClick={aoNavegar}>
            Login
          </Link>
        </li>
      </ul>

      <Link to="/login" className="botao-login">
        Login
      </Link>
    </nav>
  );
}
