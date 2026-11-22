export default function ColorSwatch({ nome, hex, classe }) {
  return (
    <div className="amostra-cor">
      <div className={`bloco ${classe}`}></div>
      <div className="rotulo">
        <strong>{nome}</strong>
        <code>{hex}</code>
      </div>
    </div>
  );
}
