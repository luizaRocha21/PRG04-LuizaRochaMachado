import { fontes } from "../data/fontes";

export default function FontTable() {
  return (
    <table className="tabela-paleta">
      <thead>
        <tr>
          <th>Uso</th>
          <th>Fonte</th>
          <th>Motivo</th>
        </tr>
      </thead>
      <tbody>
        {fontes.map((fonte) => (
          <tr key={fonte.variavel}>
            <td>
              {fonte.uso} (<code>{fonte.variavel}</code>)
            </td>
            <td style={{ fontFamily: `'${fonte.familia}', sans-serif` }}>
              {fonte.familia}
            </td>
            <td>{fonte.motivo}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
