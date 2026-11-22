import Hero from "../components/Hero";
import FeatureCard from "../components/FeatureCard";
import PageLinks from "../components/PageLinks";
import { destaques } from "../data/destaques";
import useTitulo from "../hooks/useTitulo";

export default function Home() {
  useTitulo("Início");

  return (
    <>
      <Hero />

      <section className="secao container">
        <div className="row g-4">
          {destaques.map((destaque) => (
            <div className="col-12 col-md-4" key={destaque.id}>
              <FeatureCard titulo={destaque.titulo} texto={destaque.texto} />
            </div>
          ))}
        </div>
      </section>

      <section className="secao container" aria-labelledby="titulo-sobre">
        <div className="grade-conteudo">
          <article className="cartao">
            <h2 id="titulo-sobre">Sobre esta versão</h2>
            <p>
              Esta versão do projeto é uma aplicação React, com navegação entre
              páginas feita pelo React Router e Bootstrap para grid e
              componentes.
            </p>
          </article>

          <PageLinks />
        </div>
      </section>
    </>
  );
}
