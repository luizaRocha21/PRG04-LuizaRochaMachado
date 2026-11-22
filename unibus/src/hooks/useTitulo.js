import { useEffect } from "react";

// Define o título da aba do navegador para a página atual
export default function useTitulo(titulo) {
  useEffect(() => {
    document.title = `UniBus — ${titulo}`;
  }, [titulo]);
}
