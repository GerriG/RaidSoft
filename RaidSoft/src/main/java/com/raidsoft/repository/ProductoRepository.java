package com.raidsoft.repository;

import com.raidsoft.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Método existente para el dashboard de admin
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo")
    List<Producto> findProductosByStockCritico();

    // --- NUEVO: Método para el buscador del vendedor ---
    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.codigoBarras) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Producto> search(String query);
}