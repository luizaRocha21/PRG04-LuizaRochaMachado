import { useState } from "react";
import { Link } from "react-router-dom";
import Navbar from "./Navbar";

const logo = `${import.meta.env.BASE_URL}favicon.svg`;

export default function Header() {
  const [menuAberto, setMenuAberto] = useState(false);

  return (
    <header className="cabecalho">
      <div className="container">
        <Link to="/" className="marca">
          <img src={logo} alt="Logo UniBus" />
          UniBus
        </Link>

        <button
          className="botao-menu"
          type="button"
          aria-label="Abrir menu de navegação"
          aria-expanded={menuAberto}
          aria-controls="menu"
          onClick={() => setMenuAberto((atual) => !atual)}
        >
          ☰
        </button>

        <Navbar aberto={menuAberto} aoNavegar={() => setMenuAberto(false)} />
      </div>
    </header>
  );
}
