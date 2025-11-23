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
import java.util.UUID;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    // Guardamos en la carpeta 'Productos' dentro de static para diferenciarlo de 'Profiles'
    private final String UPLOAD_DIR = System.getProperty("user.dir") + "/src/main/resources/static/Productos/";

    public void guardarProducto(Producto producto, MultipartFile archivo) throws IOException {

        // 1. Manejo de la Imagen (Si se sube una nueva)
        if (archivo != null && !archivo.isEmpty()) {

            // A) Lógica de borrado de imagen anterior (Solo si estamos editando)
            // Verificamos si el producto ya tiene ID (es edición) y tiene una URL guardada
            if (producto.getIdProducto() != null) {
                Producto productoAntiguo = productoRepository.findById(producto.getIdProducto()).orElse(null);
                
                if (productoAntiguo != null && productoAntiguo.getImagenUrl() != null) {
                    String imagenAnteriorUrl = productoAntiguo.getImagenUrl();
                    
                    // Evitamos borrar imágenes por defecto o externas si tuvieras esa lógica
                    if (!imagenAnteriorUrl.isEmpty() && imagenAnteriorUrl.startsWith("/Productos/")) {
                        try {
                            // Limpiar URL: "/Productos/foto.jpg" -> "foto.jpg"
                            String nombreArchivoAnterior = imagenAnteriorUrl.replace("/Productos/", "");
                            Path rutaArchivoAnterior = Paths.get(UPLOAD_DIR, nombreArchivoAnterior);
                            
                            Files.deleteIfExists(rutaArchivoAnterior);
                        } catch (IOException e) {
                            System.err.println("Error al borrar imagen antigua del producto: " + e.getMessage());
                        }
                    }
                }
            }

            // B) Guardar la nueva imagen
            Path rutaDirectorio = Paths.get(UPLOAD_DIR);
            if (!Files.exists(rutaDirectorio)) {
                Files.createDirectories(rutaDirectorio);
            }

            // Generar nombre único
            String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
            Path rutaArchivo = rutaDirectorio.resolve(nombreArchivo);

            Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            // Actualizar la URL en el Producto
            producto.setImagenUrl("/Productos/" + nombreArchivo);
        }

        // 2. Guardar el producto en la Base de Datos
        productoRepository.save(producto);
    }
}