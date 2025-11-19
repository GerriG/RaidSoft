package com.raidsoft.repository;

import com.raidsoft.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // El nombre debe coincidir exactamente con el campo de la entidad 'username'
    Optional<Usuario> findByUsername(String username);
}