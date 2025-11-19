package com.raidsoft.service;

import com.raidsoft.model.Usuario;
import com.raidsoft.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Buscamos por el nuevo campo 'username' definido en la Entidad
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // 2. Convertimos el Enum Rol a una autoridad de Spring
        // Ejemplo: Si el Enum es ADMINISTRADOR, Spring recibirá "ROLE_ADMINISTRADOR"
        String rolSpring = "ROLE_" + usuario.getRol().name();

        // 3. Construimos el objeto User de Spring Security
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword()) // Pasamos el hash directo de la BD (BCrypt)
                .authorities(rolSpring)
                .disabled(!usuario.getEstado()) // Bloquea el login si estado es false (0)
                .build();
    }
}