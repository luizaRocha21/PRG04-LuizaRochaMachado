const PERFIS_VALIDOS = ["estudante", "motorista", "admin"];
const STATUS_VALIDOS = ["ativo", "inativo", "pendente"];
const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export class Usuario {
  constructor({ id = null, nome, email, perfil = "estudante", status = "ativo" } = {}) {
    this.id = id;
    this.nome = nome?.trim() || "";
    this.email = email?.trim().toLowerCase() || "";
    this.perfil = perfil;
    this.status = status;
  }

  /**
   * Valida os campos obrigatórios e regras de domínio.
   * @returns {{ valido: boolean, erros: string[] }}
   */
  validar() {
    const erros = [];

    if (!this.nome || this.nome.length < 2) {
      erros.push("O campo 'nome' é obrigatório e deve ter pelo menos 2 caracteres.");
    }

    if (!this.email || !EMAIL_REGEX.test(this.email)) {
      erros.push("O campo 'email' é obrigatório e deve ter um formato válido.");
    }

    if (!PERFIS_VALIDOS.includes(this.perfil)) {
      erros.push(`O campo 'perfil' deve ser um dos valores: ${PERFIS_VALIDOS.join(", ")}.`);
    }

    if (!STATUS_VALIDOS.includes(this.status)) {
      erros.push(`O campo 'status' deve ser um dos valores: ${STATUS_VALIDOS.join(", ")}.`);
    }

    return {
      valido: erros.length === 0,
      erros,
    };
  }

  /**
   * Serializa a instância para persistência no Firestore (sem o campo id no documento).
   */
  toFirestore() {
    return {
      nome: this.nome,
      email: this.email,
      perfil: this.perfil,
      status: this.status,
    };
  }
}

export default Usuario;
