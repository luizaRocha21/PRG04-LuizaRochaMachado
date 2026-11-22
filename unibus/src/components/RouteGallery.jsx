import { useState } from "react";
import { rotas } from "../data/rotas";

export default function RouteGallery() {
  const [indice, setIndice] = useState(0);
  const rota = rotas[indice];

  // Avança para a próxima rota e volta à primeira ao chegar na última
  function proximaRota() {
    setIndice((atual) => (atual + 1) % rotas.length);
  }

  return (
    <section className="imagem-dinamica-wrap secao">
      <h2>Frota em diferentes horários</h2>
      <img src={rota.src} alt={rota.alt} />
      <p aria-live="polite">{rota.legenda}</p>
      <button className="botao" type="button" onClick={proximaRota}>
        Ver próximo horário →
      </button>
    </section>
  );
}
