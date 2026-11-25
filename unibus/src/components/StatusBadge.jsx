const estilos = {
  ativo: "bg-success",
  inativo: "bg-secondary",
  pendente: "bg-warning text-dark",
};

export default function StatusBadge({ status }) {
  const chave = typeof status === "string" ? status.toLowerCase() : "";
  return (
    <span className={`badge ${estilos[chave] ?? "bg-secondary"}`}>{status}</span>
  );
}
