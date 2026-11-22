import ColorSwatch from "../components/ColorSwatch";
import FontTable from "../components/FontTable";
import { cores } from "../data/cores";
import useTitulo from "../hooks/useTitulo";
import "../styles/paleta.css";

export default function PaletaCores() {
  useTitulo("Paleta de Cores");

  return (
    <div className="container secao">
      <div className="paleta-intro">
        <h1>Paleta de cores e fontes do UniBus</h1>
        <p>
          Esta página documenta a paleta de cores adotada em todo o projeto e a
          psicologia por trás da cor principal escolhida.
        </p>
      </div>

      <article className="cartao paleta-tipo">
        <h2>Tipo de paleta adotada</h2>
        <p>
          O projeto usa uma <strong>paleta de destaque (accent-based)</strong>:
          uma base neutra em tons de creme e preto, sobre a qual o amarelo
          aparece como cor de destaque em botões, links ativos e elementos de
          marca. Sobre essa base, existe ainda uma{" "}
          <strong>paleta funcional inspirada no semáforo</strong> (verde / âmbar
          / vermelho), usada para comunicar estados do sistema — por exemplo,
          "ônibus a caminho" (verde), "atenção/atraso" (âmbar) e
          "cancelado/alerta" (vermelho).
        </p>
      </article>

      <section className="secao" aria-labelledby="titulo-amostras">
        <h2 id="titulo-amostras">Amostras da paleta completa</h2>
        <div className="grade-paleta">
          {cores.map((cor) => (
            <ColorSwatch key={cor.hex} nome={cor.nome} hex={cor.hex} classe={cor.classe} />
          ))}
        </div>
        <p>
          Cada amostra acima usa uma classe utilitária de CSS (
          <code>.bg-amarelo</code>, <code>.bg-verde</code> etc.) definida em{" "}
          <code>src/styles/global.css</code>, que aplica a respectiva variável
          de cor declarada em <code>:root</code>.
        </p>
      </section>

      <section className="secao" aria-labelledby="titulo-fontes">
        <h2 id="titulo-fontes">Fontes do projeto</h2>
        <FontTable />
      </section>
    </div>
  );
}
