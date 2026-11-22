import { Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import Home from "./pages/Home";
import ComoFunciona from "./pages/ComoFunciona";
import PaletaCores from "./pages/PaletaCores";
import Sandbox from "./pages/Sandbox";
import Login from "./pages/Login";
import Admin from "./pages/Admin";
import NotFound from "./pages/NotFound";

// Rotas da aplicação; todas as páginas usam o Layout (cabeçalho e rodapé)
export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<Home />} />
        <Route path="como-funciona" element={<ComoFunciona />} />
        <Route path="paleta-cores" element={<PaletaCores />} />
        <Route path="sandbox" element={<Sandbox />} />
        <Route path="login" element={<Login />} />
        <Route path="admin" element={<Admin />} />
        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  );
}
