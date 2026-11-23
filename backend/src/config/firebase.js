import admin from "firebase-admin";
import { readFileSync } from "node:fs";
import { resolve, dirname } from "node:path";
import { fileURLToPath } from "node:url";
import "dotenv/config";

const __dirname = dirname(fileURLToPath(import.meta.url));

/**
 * Carrega as credenciais do Firebase via variável de ambiente (produção)
 * ou arquivo local serviceAccountKey.json (desenvolvimento).
 */
function carregarCredenciais() {
  if (process.env.FIREBASE_SERVICE_ACCOUNT) {
    return JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT);
  }

  const caminhoChave = resolve(__dirname, "../../serviceAccountKey.json");
  return JSON.parse(readFileSync(caminhoChave, "utf-8"));
}

if (!admin.apps.length) {
  admin.initializeApp({
    credential: admin.credential.cert(carregarCredenciais()),
  });
}

export const db = admin.firestore();
export default admin;
