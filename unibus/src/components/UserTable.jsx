import StatusBadge from "./StatusBadge";

export default function UserTable({ usuarios }) {
  return (
    <div className="cartao table-responsive">
      <table className="table table-striped table-hover align-middle mb-0">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nome</th>
            <th>E-mail</th>
            <th>Status</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {usuarios.map((usuario) => (
            <tr key={usuario.id}>
              <td>{usuario.id}</td>
              <td>{usuario.nome}</td>
              <td>{usuario.email}</td>
              <td>
                <StatusBadge status={usuario.status} />
              </td>
              <td>
                <div className="d-flex gap-2">
                  <button type="button" className="btn btn-sm btn-outline-primary">
                    Editar
                  </button>
                  <button type="button" className="btn btn-sm btn-outline-danger">
                    Excluir
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
