// Cor do selo conforme o status do usuário
const estilos = {
  Ativo: "bg-success",
  Inativo: "bg-secondary",
  Pendente: "bg-warning text-dark",
};

export default function StatusBadge({ status }) {
  return (
    <span className={`badge ${estilos[status] ?? "bg-secondary"}`}>{status}</span>
  );
}
