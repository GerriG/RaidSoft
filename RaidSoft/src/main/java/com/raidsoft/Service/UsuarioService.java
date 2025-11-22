package com.raidsoft.service;

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

    // Usamos la ruta absoluta para evitar errores de "archivo no encontrado" en Windows
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/Profiles/";

    public void actualizarPerfil(Usuario usuario, Usuario datosFormulario, MultipartFile archivo, String nuevaPassword) throws IOException {
        
        // 1. Actualizar datos básicos
        if (datosFormulario.getNombre() != null && !datosFormulario.getNombre().isEmpty()) {
            usuario.setNombre(datosFormulario.getNombre());
        }
        if (datosFormulario.getApellido() != null && !datosFormulario.getApellido().isEmpty()) {
            usuario.setApellido(datosFormulario.getApellido());
        }

        // 2. Actualizar Contraseña (solo si se envía)
        if (nuevaPassword != null && !nuevaPassword.isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(nuevaPassword));
        }

        // 3. Manejo de la Imagen
        if (archivo != null && !archivo.isEmpty()) {
            
            // --- LÓGICA DE BORRADO (Adaptada de tu proyecto anterior) ---
            String imagenAnteriorUrl = usuario.getImagenUrl();
            
            // Solo borramos si hay URL, no está vacía y NO es la imagen por defecto (si la tuvieras)
            if (imagenAnteriorUrl != null && !imagenAnteriorUrl.isEmpty()) {
                try {
                    // La URL en BD es "/Profiles/foto.jpg". Quitamos "/Profiles/" para obtener el nombre real
                    String nombreArchivoAnterior = imagenAnteriorUrl.replace("/Profiles/", "");
                    
                    // Construimos la ruta al archivo físico
                    Path rutaArchivoAnterior = Paths.get(UPLOAD_DIR, nombreArchivoAnterior);
                    
                    // Intentamos borrar. deleteIfExists devuelve true si borró, false si no existía
                    boolean borrado = Files.deleteIfExists(rutaArchivoAnterior);
                    
                    if(borrado) {
                        System.out.println("✅ Imagen anterior eliminada: " + nombreArchivoAnterior);
                    } else {
                        System.out.println("⚠️ No se encontró el archivo anterior para borrar: " + rutaArchivoAnterior);
                    }
                    
                } catch (IOException e) {
                    System.err.println("❌ Error al intentar borrar imagen anterior: " + e.getMessage());
                    // No lanzamos throw para permitir que se guarde la nueva aunque falle el borrado de la vieja
                }
            }

            // --- LÓGICA DE GUARDADO DE LA NUEVA IMAGEN ---
            Path rutaDirectorio = Paths.get(UPLOAD_DIR);
            if (!Files.exists(rutaDirectorio)) {
                Files.createDirectories(rutaDirectorio);
            }

            String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
            Path rutaArchivo = rutaDirectorio.resolve(nombreArchivo);
            
            Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
            
            // Guardamos la nueva URL en el usuario
            usuario.setImagenUrl("/Profiles/" + nombreArchivo);
            System.out.println("✅ Nueva imagen guardada: " + nombreArchivo);
        }

        // 4. Guardar cambios en Base de Datos
        usuarioRepository.save(usuario);
    }
}