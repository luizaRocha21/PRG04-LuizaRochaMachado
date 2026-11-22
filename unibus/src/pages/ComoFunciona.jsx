import StepList from "../components/StepList";
import RouteGallery from "../components/RouteGallery";
import useTitulo from "../hooks/useTitulo";
import "../styles/como-funciona.css";

export default function ComoFunciona() {
  useTitulo("Como funciona");

  return (
    <div className="container secao">
      <article>
        <h1>Como o UniBus vai funcionar</h1>
        <p>
          Esta página apresenta, de forma resumida, o funcionamento planejado
          para a plataforma UniBus — o transporte universitário com rastreio em
          tempo real.
        </p>

        <section>
          <h2>Passo a passo do embarque</h2>
          <StepList />
        </section>

        <RouteGallery />
      </article>
    </div>
  );
}
