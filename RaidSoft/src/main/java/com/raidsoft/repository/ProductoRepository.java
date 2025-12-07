package com.raidsoft.repository;

import com.raidsoft.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {  

    // Para la tabla de reporte de stock crítico
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo")
    List<Producto> findProductosByStockCritico();

    // Para el buscador del vendedor
    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.codigoBarras) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Producto> search(String query);
   
    // 1. Contar cuántos productos están en alerta
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.stock <= p.stockMinimo")
    long contarProductosBajoStock();

    // 2. Sumar todo el stock total
    @Query("SELECT COALESCE(SUM(p.stock), 0) FROM Producto p")
    long sumarStockTotal();    

    // Busca productos activos cuyo stock es menor o igual al mínimo, ideal para reabastecimiento
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.estado = true ORDER BY p.stock ASC")
    List<Producto> encontrarProductosParaReabastecer();
}