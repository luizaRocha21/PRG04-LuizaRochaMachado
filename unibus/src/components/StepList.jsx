import { passos } from "../data/passos";

export default function StepList() {
  return (
    <ul className="lista-passos">
      {passos.map((passo) => (
        <li key={passo.id}>
          <strong>{passo.titulo}</strong> — {passo.texto}
        </li>
      ))}
    </ul>
  );
}
