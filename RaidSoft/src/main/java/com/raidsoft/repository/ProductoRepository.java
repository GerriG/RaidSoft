package com.raidsoft.repository;

import com.raidsoft.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // --- MÉTODOS EXISTENTES (Buscador y Lista Crítica) ---

    // Para la tabla de reporte de stock crítico
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo")
    List<Producto> findProductosByStockCritico();

    // Para el buscador del vendedor
    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.codigoBarras) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Producto> search(String query);

    // --- NUEVOS MÉTODOS PARA EL DASHBOARD (Tarjetas con números) ---

    // 1. Contar cuántos productos están en alerta (Devuelve un número, ej: 8)
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock <= p.stockMinimo")
    long contarProductosBajoStock();

    // 2. Sumar todo el stock físico disponible (Devuelve un número, ej: 5430)
    // Usamos COALESCE para que si no hay productos devuelva 0 en vez de null
    @Query("SELECT COALESCE(SUM(p.stock), 0) FROM Producto p")
    long sumarStockTotal();
}