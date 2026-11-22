import MediaBlock from "../components/MediaBlock";
import useTitulo from "../hooks/useTitulo";
import "../styles/sandbox.css";

export default function Sandbox() {
  useTitulo("SandBox");

  return (
    <div className="container secao">
      <div className="sandbox-intro">
        <span className="selo-sandbox">SandBox</span>
        <h1>Página de testes do projeto</h1>
        <p>
          Espaço reservado para testar conceitos aprendidos em aula, sem afetar
          as páginas "oficiais" do projeto.
        </p>
      </div>

      <MediaBlock
        titulo="🎵 Áudio"
        descricao={
          <>Elemento &lt;audio&gt; com controles, arquivo externo (exemplo didático):</>
        }
      >
        <audio controls>
          <source src="https://www.w3schools.com/html/horse.ogg" type="audio/ogg" />
          <source src="https://www.w3schools.com/html/horse.mp3" type="audio/mpeg" />
          Seu navegador não suporta o elemento de áudio.
        </audio>
      </MediaBlock>

      <MediaBlock titulo="🎬 Vídeo" descricao="Vídeo externo embutido via iframe do YouTube:">
        <div className="video-wrap">
          <iframe
            src="https://www.youtube.com/embed/Zh-ZUrc-aLI"
            title="Vídeo incorporado do YouTube"
            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
            allowFullScreen
          ></iframe>
        </div>
      </MediaBlock>
    </div>
  );
}
