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

    // ============================================================
    // 1. MÉTODOS PARA ESTADÍSTICAS DEL DASHBOARD (Los que faltaban)
    // ============================================================
    
    public long contarTotalProductos() {
        return productoRepository.count();
    }

    public long contarAlertasStock() {
        // Este método requiere que hayas actualizado ProductoRepository con @Query
        return productoRepository.contarProductosBajoStock();
    }

    public long obtenerStockTotal() {
        // Este método requiere que hayas actualizado ProductoRepository con @Query
        return productoRepository.sumarStockTotal();
    }

    // ============================================================
    // 2. LOGICA DE GUARDADO DE PRODUCTO E IMÁGENES
    // ============================================================

    public void guardarProducto(Producto producto, MultipartFile archivo) throws IOException {

        // A. Corrección para el Código de Barras (Evitar vacíos "")
        if (producto.getCodigoBarras() != null && producto.getCodigoBarras().trim().isEmpty()) {
            producto.setCodigoBarras(null);
        }

        // B. Manejo de la Imagen (Solo si se sube una nueva)
        if (archivo != null && !archivo.isEmpty()) {
            
            // Validación de formato
            String contentType = archivo.getContentType();
            if (!FORMATOS_PERMITIDOS.contains(contentType)) {
                throw new IllegalArgumentException("Formato no válido. Solo se permiten: JPG, PNG, GIF, WEBP o AVIF.");
            }

            // Borrado de imagen anterior (si existe y es edición)
            if (producto.getIdProducto() != null) {
                Producto productoAntiguo = productoRepository.findById(producto.getIdProducto()).orElse(null);
                
                if (productoAntiguo != null && productoAntiguo.getImagenUrl() != null) {
                    String urlAntigua = productoAntiguo.getImagenUrl();
                    
                    if (!urlAntigua.isEmpty() && urlAntigua.startsWith("/Productos/")) {
                        try {
                            String nombreArchivoAnterior = urlAntigua.replace("/Productos/", "");
                            Path rutaArchivoAnterior = Paths.get(UPLOAD_DIR, nombreArchivoAnterior);
                            Files.deleteIfExists(rutaArchivoAnterior);
                        } catch (IOException e) {
                            System.err.println("⚠️ No se pudo borrar la imagen anterior: " + e.getMessage());
                        }
                    }
                }
            }

            // Guardado de la NUEVA imagen
            try {
                Path rutaDirectorio = Paths.get(UPLOAD_DIR);
                if (!Files.exists(rutaDirectorio)) {
                    Files.createDirectories(rutaDirectorio);
                }

                String nombreArchivo = UUID.randomUUID().toString() + "_" + archivo.getOriginalFilename();
                Path rutaArchivo = rutaDirectorio.resolve(nombreArchivo);

                Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
                
                // Actualizar URL en la entidad
                producto.setImagenUrl("/Productos/" + nombreArchivo);
                
            } catch (IOException e) {
                throw new IOException("Error al guardar la imagen en el servidor.");
            }
        } 
        // Si no se subió imagen, se mantiene la URL actual (JPA lo maneja si el campo no cambia)

        // C. Guardar en Base de Datos
        productoRepository.save(producto);
    }
}