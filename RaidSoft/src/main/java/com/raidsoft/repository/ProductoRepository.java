package com.raidsoft.repository;

import com.raidsoft.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Aquí puedes agregar métodos de búsqueda personalizados si los necesitas a futuro
    // Ejemplo: List<Producto> findByNombreContaining(String nombre);
}