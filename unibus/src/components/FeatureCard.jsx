export default function FeatureCard({ titulo, texto }) {
  return (
    <div className="card h-100 cartao-bootstrap">
      <div className="card-body">
        <h3 className="card-title h5">{titulo}</h3>
        <p className="card-text">{texto}</p>
      </div>
    </div>
  );
}
