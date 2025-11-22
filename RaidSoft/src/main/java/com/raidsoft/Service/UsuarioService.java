package com.raidsoft.service;

import com.raidsoft.model.Perfil;
import com.raidsoft.model.Usuario;
import com.raidsoft.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Ruta absoluta para evitar errores de "archivo no encontrado" en Windows
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/Profiles/";

    public void actualizarPerfil(Usuario usuario, Perfil datosFormulario, MultipartFile archivo, String nuevaPassword) throws IOException {
        
        // 1. Obtener el perfil actual de la base de datos
        Perfil perfilActual = usuario.getPerfil();
        
        // Validación de seguridad por si el usuario no tuviera perfil creado (casos raros)
        if (perfilActual == null) {
            perfilActual = new Perfil();
            perfilActual.setUsuario(usuario);
            usuario.setPerfil(perfilActual);
        }

        // 2. Actualizar datos básicos en el objeto PERFIL
        if (datosFormulario.getNombre() != null && !datosFormulario.getNombre().isEmpty()) {
            perfilActual.setNombre(datosFormulario.getNombre());
        }
        if (datosFormulario.getApellido() != null && !datosFormulario.getApellido().isEmpty()) {
            perfilActual.setApellido(datosFormulario.getApellido());
        }
        if (datosFormulario.getEmail() != null && !datosFormulario.getEmail().isEmpty()) {
            perfilActual.setEmail(datosFormulario.getEmail());
        }
        if (datosFormulario.getFechaNacimiento() != null) {
            perfilActual.setFechaNacimiento(datosFormulario.getFechaNacimiento());
        }

        // 3. Actualizar Contraseña en el objeto USUARIO (solo si se envía)
        if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        }

        // 4. Manejo de la Imagen (Lógica de borrado + subida)
        if (archivo != null && !archivo.isEmpty()) {
            
            // A) Intentar borrar la imagen anterior del disco
            String imagenAnteriorUrl = perfilActual.getAvatarUrl(); // Ahora obtenemos la URL del Perfil
            
            if (imagenAnteriorUrl != null && !imagenAnteriorUrl.isEmpty() && !imagenAnteriorUrl.contains("default")) {
                try {
                    // Limpiar URL: "/Profiles/foto.jpg" -> "foto.jpg"
                    String nombreArchivoAnterior = imagenAnteriorUrl.replace("/Profiles/", "");
                    
                    // Construir ruta absoluta
                    Path rutaArchivoAnterior = Paths.get(UPLOAD_DIR, nombreArchivoAnterior);
                    
                    // Borrar si existe
                    boolean borrado = Files.deleteIfExists(rutaArchivoAnterior);
                    
                    if(borrado) {
                        System.out.println("✅ Imagen anterior eliminada: " + nombreArchivoAnterior);
                    }
                } catch (IOException e) {
                    System.err.println("❌ Error no crítico al borrar imagen anterior: " + e.getMessage());
                }
            }

            // B) Guardar la nueva imagen
            Path rutaDirectorio = Paths.get(UPLOAD_DIR);
            if (!Files.exists(rutaDirectorio)) {
                Files.createDirectories(rutaDirectorio);
            }

            String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
            Path rutaArchivo = rutaDirectorio.resolve(nombreArchivo);
            
            Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
            
            // Actualizar la URL en el Perfil
            perfilActual.setAvatarUrl("/Profiles/" + nombreArchivo);
            System.out.println("✅ Nueva imagen guardada: " + nombreArchivo);
        }

        // 5. Guardar cambios (Al guardar Usuario, JPA actualiza Perfil por la cascada)
        usuarioRepository.save(usuario);
    }
}