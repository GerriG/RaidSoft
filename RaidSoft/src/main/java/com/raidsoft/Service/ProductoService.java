package com.raidsoft.service;

import com.raidsoft.model.Producto;
import com.raidsoft.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Ruta absoluta a la carpeta de imágenes
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/Productos/";

    // Lista de formatos permitidos
    private final List<String> FORMATOS_PERMITIDOS = Arrays.asList("image/jpeg", "image/png", "image/gif", "image/webp", "image/avif");

    public void guardarProducto(Producto producto, MultipartFile archivo) throws IOException {

        // 1. Corrección para el Código de Barras (Opcional)
        // Si viene vacío, lo seteamos a NULL para evitar el error de "Duplicate entry"
        if (producto.getCodigoBarras() != null && producto.getCodigoBarras().trim().isEmpty()) {
            producto.setCodigoBarras(null);
        }

        // 2. Manejo de la Imagen (Solo si el usuario subió un archivo nuevo)
        if (archivo != null && !archivo.isEmpty()) {
            
            // A) Validación de formato (Seguridad)
            String contentType = archivo.getContentType();
            if (!FORMATOS_PERMITIDOS.contains(contentType)) {
                throw new IllegalArgumentException("Formato no válido. Solo se permiten: JPG, PNG, GIF, WEBP o AVIF.");
            }

            // B) LÓGICA DE BORRADO DE IMAGEN ANTERIOR
            // Verificamos si es una edición (tiene ID)
            if (producto.getIdProducto() != null) {
                // Buscamos la versión actual en BD antes de sobreescribirla
                Producto productoAntiguo = productoRepository.findById(producto.getIdProducto()).orElse(null);
                
                if (productoAntiguo != null && productoAntiguo.getImagenUrl() != null) {
                    String urlAntigua = productoAntiguo.getImagenUrl();
                    
                    // Solo borramos si la URL parece válida y pertenece a nuestro sistema
                    if (!urlAntigua.isEmpty() && urlAntigua.startsWith("/Productos/")) {
                        try {
                            // Convertimos URL web ("/Productos/foto.jpg") a nombre de archivo ("foto.jpg")
                            String nombreArchivoAnterior = urlAntigua.replace("/Productos/", "");
                            
                            // Construimos la ruta física completa
                            Path rutaArchivoAnterior = Paths.get(UPLOAD_DIR, nombreArchivoAnterior);
                            
                            // Debug: Ver en consola qué está pasando
                            System.out.println("--- INTENTANDO BORRAR IMAGEN ---");
                            System.out.println("Ruta: " + rutaArchivoAnterior.toAbsolutePath());

                            // Intentamos borrar
                            boolean borrado = Files.deleteIfExists(rutaArchivoAnterior);
                            
                            if (borrado) {
                                System.out.println("✅ Imagen anterior eliminada con éxito.");
                            } else {
                                System.out.println("⚠️ No se encontró el archivo anterior o no se pudo borrar.");
                            }
                            
                        } catch (IOException e) {
                            // Si falla el borrado (archivo bloqueado, etc.), no detenemos el guardado del producto
                            System.err.println("❌ Error al borrar imagen antigua (no crítico): " + e.getMessage());
                        }
                    }
                }
            }

            // C) Guardar la NUEVA imagen
            try {
                // Asegurar que el directorio existe
                Path rutaDirectorio = Paths.get(UPLOAD_DIR);
                if (!Files.exists(rutaDirectorio)) {
                    Files.createDirectories(rutaDirectorio);
                }

                // Generar nombre único para evitar colisiones
                String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
                Path rutaArchivo = rutaDirectorio.resolve(nombreArchivo);

                // Guardar archivo en disco
                Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("✅ Nueva imagen guardada: " + nombreArchivo);

                // Actualizar la URL en el objeto Producto
                producto.setImagenUrl("/Productos/" + nombreArchivo);
                
            } catch (IOException e) {
                throw new IOException("Error de lectura/escritura al guardar la nueva imagen. Intenta de nuevo.");
            }
        } 
        // Si no se subió imagen nueva, mantenemos la URL que ya tenía el producto (se maneja automáticamente por el formulario)

        // 3. Guardar el producto (Datos + URL de imagen actualizada)
        productoRepository.save(producto);
    }
}