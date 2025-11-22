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
        // 1. Buscar usuario en la Base de Datos
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // 2. Convertir Rol (Enum -> Spring Authority)
        String authority = usuario.getRol().name();

        // 3. Retornar objeto User de Spring Security
        // CORRECCIÓN CRÍTICA: La contraseña en la DB ya tiene el prefijo {bcrypt},
        // por lo tanto, la pasamos directamente sin modificar.
        String passwordAlmacenada = usuario.getPassword(); 
        
        return User.builder()
                .username(usuario.getUsername())
                .password(passwordAlmacenada) // CAMBIO: Se pasa directamente.
                .authorities(authority) 
                .disabled(!usuario.getEstado()) 
                .build();
    }
}