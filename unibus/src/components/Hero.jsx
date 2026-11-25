import { Link } from "react-router-dom";

export default function Hero() {
  return (
    <section className="hero container" id="inicio">
      <span className="pilula">
        <span className="ponto-verde"></span>
        Ônibus universitário — rastreio em tempo real
      </span>

      <h1>
        O trajeto seguro da <span className="destaque">sua faculdade.</span>
      </h1>

      <p>
        <strong>UniBus</strong> é uma plataforma de transporte universitário
        com rastreio ao vivo, motoristas verificados e avisos instantâneos para
        estudantes.
      </p>

      <Link to="/como-funciona" className="botao">
        Conhecer o projeto →
      </Link>
    </section>
  );
}
