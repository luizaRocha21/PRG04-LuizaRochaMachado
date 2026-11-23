import { db } from "../config/firebase.js";
import { Usuario } from "../models/Usuario.js";

const COLECAO = "usuarios";

export async function listarUsuarios(req, res) {
  try {
    const snapshot = await db.collection(COLECAO).get();
    const usuarios = snapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));

    return res.status(200).json(usuarios);
  } catch (erro) {
    return res.status(500).json({ erro: "Erro ao listar usuários.", detalhes: erro.message });
  }
}

export async function buscarUsuarioPorId(req, res) {
  try {
    const { id } = req.params;
    const doc = await db.collection(COLECAO).doc(id).get();

    if (!doc.exists) {
      return res.status(404).json({ erro: "Usuário não encontrado." });
    }

    return res.status(200).json({ id: doc.id, ...doc.data() });
  } catch (erro) {
    return res.status(500).json({ erro: "Erro ao buscar usuário.", detalhes: erro.message });
  }
}

export async function criarUsuario(req, res) {
  try {
    const novoUsuario = new Usuario(req.body);
    const { valido, erros } = novoUsuario.validar();

    if (!valido) {
      return res.status(400).json({ erro: "Dados inválidos.", detalhes: erros });
    }

    const docRef = await db.collection(COLECAO).add(novoUsuario.toFirestore());

    return res.status(201).json({
      id: docRef.id,
      ...novoUsuario.toFirestore(),
    });
  } catch (erro) {
    return res.status(500).json({ erro: "Erro ao criar usuário.", detalhes: erro.message });
  }
}

export async function atualizarUsuario(req, res) {
  try {
    const { id } = req.params;
    const docRef = db.collection(COLECAO).doc(id);
    const doc = await docRef.get();

    if (!doc.exists) {
      return res.status(404).json({ erro: "Usuário não encontrado." });
    }

    const usuarioAtualizado = new Usuario({
      ...doc.data(),
      ...req.body,
      id,
    });

    const { valido, erros } = usuarioAtualizado.validar();
    if (!valido) {
      return res.status(400).json({ erro: "Dados inválidos.", detalhes: erros });
    }

    await docRef.update(usuarioAtualizado.toFirestore());

    return res.status(200).json({
      id,
      ...usuarioAtualizado.toFirestore(),
    });
  } catch (erro) {
    return res.status(500).json({ erro: "Erro ao atualizar usuário.", detalhes: erro.message });
  }
}

export async function deletarUsuario(req, res) {
  try {
    const { id } = req.params;
    const docRef = db.collection(COLECAO).doc(id);
    const doc = await docRef.get();

    if (!doc.exists) {
      return res.status(404).json({ erro: "Usuário não encontrado." });
    }

    await docRef.delete();

    return res.status(200).json({ mensagem: "Usuário removido com sucesso.", id });
  } catch (erro) {
    return res.status(500).json({ erro: "Erro ao deletar usuário.", detalhes: erro.message });
  }
}
