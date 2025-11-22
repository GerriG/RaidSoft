package com.raidsoft.repository;

import com.raidsoft.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- ¡IMPORTANTE!
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    // [ELIMINAR: List<Producto> findByStockLessThanEqualStockMinimo();]

    /**
     * CORRECCIÓN: Usa JPQL para comparar el campo 'stock' con el campo 'stockMinimo'.
     * HHH000412: El Error de consulta JPA ha sido corregido con la anotación @Query
     */
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo")
    List<Producto> findProductosByStockCritico(); 
}