package com.unibus.backend.repository;

import com.unibus.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Acesso a dados da entidade {@link Usuario}.
 *
 * <p>O {@link JpaRepository} já fornece o CRUD completo (save, findById, findAll, deleteById...).
 * Aqui ficam apenas as consultas específicas, geradas pelo Spring Data a partir do nome do método.</p>
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /** Indica se já existe algum usuário com o e-mail informado. */
    boolean existsByEmail(String email);

    /** Indica se OUTRO usuário (id diferente do informado) já usa o e-mail. Usado na atualização. */
    boolean existsByEmailAndIdNot(String email, Long id);
}
