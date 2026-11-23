import express from "express";
import cors from "cors";
import "dotenv/config";
import usuarioRoutes from "./routes/usuarioRoutes.js";

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

app.get("/", (req, res) => {
  res.status(200).json({ status: "online", servico: "UniBus API" });
});

app.use("/api/usuarios", usuarioRoutes);

// Inicia o servidor localmente (fora do ambiente serverless da Vercel)
if (!process.env.VERCEL) {
  app.listen(PORT, () => {
    console.log(`Servidor UniBus rodando em http://localhost:${PORT}`);
  });
}

export default app;
