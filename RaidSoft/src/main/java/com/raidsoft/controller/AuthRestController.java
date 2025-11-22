package com.raidsoft.controller;

import com.raidsoft.dto.RegistroRequest;
import com.raidsoft.model.Perfil;
import com.raidsoft.model.Rol;
import com.raidsoft.model.Usuario;
import com.raidsoft.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Endpoint para manejar la solicitud de registro JSON
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
            
            // 3. Creación y asociación del Perfil (con todos los campos obligatorios)
            Perfil nuevoPerfil = new Perfil();
            
            // Asignación de datos obligatorios del Perfil (nombre, apellido)
            if (request.getNombre() == null || request.getNombre().isEmpty()) {
                nuevoPerfil.setNombre(request.getUsername());
            } else {
                nuevoPerfil.setNombre(request.getNombre());
            }
            if (request.getApellido() == null || request.getApellido().isEmpty()) {
                nuevoPerfil.setApellido("(Sin Apellido)");
            } else {
                nuevoPerfil.setApellido(request.getApellido());
            }

            nuevoPerfil.setEmail(request.getEmail());
            // Asignación de la fecha de nacimiento (nuevo requisito)
            nuevoPerfil.setFechaNacimiento(request.getFechaNacimiento());
            
            // Establecer la relación bidireccional (Usuario tiene setter para esto)
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
}