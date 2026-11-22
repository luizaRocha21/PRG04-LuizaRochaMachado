import { Link } from "react-router-dom";
import { linksExplorar } from "../data/menu";

export default function PageLinks() {
  return (
    <aside className="cartao aside-nota" aria-label="Páginas do projeto">
      <h2>Explore o projeto</h2>
      <ul>
        {linksExplorar.map((link) => (
          <li key={link.para}>
            <Link to={link.para}>{link.rotulo}</Link>
          </li>
        ))}
      </ul>
    </aside>
  );
}
