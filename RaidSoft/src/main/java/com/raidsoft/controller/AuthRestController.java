package com.raidsoft.controller;

import com.raidsoft.dto.RegistroRequest;
import com.raidsoft.model.Perfil;
import com.raidsoft.model.Rol;
import com.raidsoft.model.Usuario;
import com.raidsoft.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =======================================================
    // 1. REGISTRO (POST /api/register)
    // =======================================================
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody RegistroRequest request) {
        Map<String, String> response = new HashMap<>();
        
        try {
            // 1. Validación de existencia
            if (usuarioRepository.findByUsername(request.getUsername()).isPresent()) {
                response.put("status", "error");
                response.put("message", "El nombre de usuario ya existe.");
                return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409 Conflict
            }
            
            // 2. Creación y encriptación de Usuario
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUsername(request.getUsername());
            nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword())); 
            nuevoUsuario.setEstado(true);
            nuevoUsuario.setRol(Rol.VENDEDOR); // Rol por defecto
            
            // 3. Creación y asociación del Perfil
            Perfil nuevoPerfil = new Perfil();
            
            nuevoPerfil.setNombre(request.getNombre() != null && !request.getNombre().isEmpty() ? request.getNombre() : request.getUsername());
            nuevoPerfil.setApellido(request.getApellido() != null && !request.getApellido().isEmpty() ? request.getApellido() : "(Sin Apellido)");
            nuevoPerfil.setEmail(request.getEmail());
            nuevoPerfil.setFechaNacimiento(request.getFechaNacimiento());
            
            nuevoUsuario.setPerfil(nuevoPerfil); 
            
            // 4. Guardar (JPA guarda el perfil en cascada)
            usuarioRepository.save(nuevoUsuario);
            
            response.put("status", "success");
            response.put("message", "¡Cuenta creada con éxito! Ahora puedes iniciar sesión.");
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error interno al procesar el registro: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    // =======================================================
    // 2. RECUPERACIÓN DE CONTRASEÑA (POST /api/reset-password)
    // =======================================================
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam("email") String email,
            @RequestParam("fechaNacimiento") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNacimiento,
            @RequestParam("newPassword") String newPassword) {

        Map<String, String> response = new HashMap<>();

        try {
            // NOTA: Usamos el método findByPerfilEmail que agregaste a UsuarioRepository
            Usuario user = usuarioRepository.findByPerfilEmail(email).orElse(null); 

            // 1. Validación de identidad
            if (user == null || 
                user.getPerfil() == null ||
                user.getPerfil().getFechaNacimiento() == null || 
                !user.getPerfil().getFechaNacimiento().equals(fechaNacimiento)) 
            {
                response.put("status", "error");
                response.put("message", "Error: El correo o la fecha de nacimiento no coinciden.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED); // 401
            }

            // 2. Validación pasada: Encriptar y guardar nueva contraseña
            user.setPassword(passwordEncoder.encode(newPassword));
            usuarioRepository.save(user);

            response.put("status", "success");
            response.put("message", "Contraseña restablecida correctamente.");
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error interno del sistema.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
}