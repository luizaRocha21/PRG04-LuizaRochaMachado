export default function MediaBlock({ titulo, descricao, children }) {
  return (
    <section className="midia-bloco">
      <h2>{titulo}</h2>
      <p>{descricao}</p>
      {children}
    </section>
  );
}
