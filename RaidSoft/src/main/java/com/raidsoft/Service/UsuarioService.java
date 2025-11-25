package com.raidsoft.service;

import com.raidsoft.model.Perfil;
import com.raidsoft.model.Usuario;
import com.raidsoft.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- IMPORTANTE
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.Optional;

@Service
public class UsuarioService {

    // Directorio base para guardar los avatares.
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/Profiles/"; 

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Puedes agregar más métodos de búsqueda si los necesitas
    public Optional<Usuario> findByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
    
    // --- LÓGICA PRINCIPAL DE ACTUALIZACIÓN ---
    @Transactional // <--- CRUCIAL: Asegura que el guardado sea atómico y la data fresca
    public void actualizarPerfil(Usuario usuarioActual, Perfil perfilForm, 
                                 MultipartFile archivo, String newPassword) throws IOException {

        // 1. Obtener o crear Perfil
        Perfil perfil = usuarioActual.getPerfil();
        
        if (perfil == null) {
            perfil = new Perfil();
            perfil.setUsuario(usuarioActual);
        }

        // 2. TRANSFERENCIA DE DATOS (Incluyendo la fecha, que soluciona el bug)
        // La validación de longitud de String se maneja en la DB/Controller.
        if (perfilForm.getNombre() != null) { perfil.setNombre(perfilForm.getNombre()); }
        if (perfilForm.getApellido() != null) { perfil.setApellido(perfilForm.getApellido()); }
        if (perfilForm.getEmail() != null) { perfil.setEmail(perfilForm.getEmail()); }
        
        // FIX: La fecha se transfiere explícitamente y es el valor validado
        if (perfilForm.getFechaNacimiento() != null) { 
            perfil.setFechaNacimiento(perfilForm.getFechaNacimiento()); 
        }

        // 3. ACTUALIZAR CONTRASEÑA (si se proporcionó)
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            usuarioActual.setPassword(passwordEncoder.encode(newPassword));
        }

        // 4. MANEJO DE IMAGEN (Avatar)
        if (archivo != null && !archivo.isEmpty() && archivo.getSize() > 0) {
            String nuevoAvatarUrl = guardarArchivo(archivo);
            
            // Opcional: Borrar el archivo antiguo si existe
            if (perfil.getAvatarUrl() != null && !perfil.getAvatarUrl().isEmpty()) {
                 borrarArchivoAntiguo(perfil.getAvatarUrl());
            }

            perfil.setAvatarUrl(nuevoAvatarUrl); 
        }

        // 5. GUARDAR (JPA guarda el Perfil por cascada)
        usuarioActual.setPerfil(perfil);
        usuarioRepository.save(usuarioActual);
    }

    // --- MÉTODOS PRIVADOS PARA MANEJO DE ARCHIVOS (Necesarios para el punto 4) ---
    
    /** Guarda el archivo subido en el directorio estático. */
    private String guardarArchivo(MultipartFile archivo) throws IOException {
        String originalFilename = archivo.getOriginalFilename();
        // Usamos la extensión del archivo original
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")); 
        String newFilename = UUID.randomUUID().toString() + extension;
        Path filePath = Paths.get(UPLOAD_DIR, newFilename);
        
        // Crea la carpeta si no existe
        Files.createDirectories(filePath.getParent()); 
        Files.copy(archivo.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Retorna la URL relativa para el HTML
        return "/Profiles/" + newFilename;
    }

    /** Borra el archivo de avatar antiguo del disco. */
    private void borrarArchivoAntiguo(String avatarUrl) {
        String filename = avatarUrl.replace("/Profiles/", "");
        Path filePath = Paths.get(UPLOAD_DIR, filename);
        
        try {
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            // Error no crítico: el archivo ya fue borrado o está bloqueado
            System.err.println("Error al borrar el archivo antiguo: " + e.getMessage());
        }
    }
}