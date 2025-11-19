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
        // Ejemplo: Si en BD es "ADMINISTRADOR", Spring necesita "ROLE_ADMINISTRADOR"
        String rolSpring = "ROLE_" + usuario.getRol().name();

        // 3. Retornar objeto User de Spring Security
        // NOTA CRÍTICA: Pasamos usuario.getPassword() DIRECTAMENTE.
        // No agregamos {noop} ni {bcrypt}. El PasswordEncoder en SecurityConfig
        // se encargará de verificar este hash contra la contraseña plana que ingresa el usuario.
        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword()) 
                .authorities(rolSpring)
                .disabled(!usuario.getEstado()) // Bloquea el acceso si estado es false (0)
                .build();
    }
}