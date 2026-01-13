package com.raidsoft.repository;

import com.raidsoft.model.Venta;
import com.raidsoft.model.Usuario;
import com.raidsoft.dto.VentaVendedorDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Historial detallado (sin cambios)
    List<Venta> findByVendedorOrderByFechaDesc(Usuario vendedor);

    // --- REPORTE CORREGIDO (LEFT JOIN) ---
    // Ahora muestra a TODOS los vendedores, aunque no hayan vendido nada.
    @Query(value = """
        SELECT 
            u.username AS username,
            CONCAT(IFNULL(p.nombre, u.username), ' ', IFNULL(p.apellido, '')) AS nombreCompleto,
            COUNT(v.id_venta) AS cantidadVentas,
            COALESCE(SUM(v.total), 0) AS totalVendido
        FROM usuarios u
        LEFT JOIN perfiles p ON u.id_usuario = p.id_usuario
        LEFT JOIN ventas v ON u.id_usuario = v.id_usuario
        WHERE u.rol = 'VENDEDOR' 
        GROUP BY u.id_usuario, u.username, p.nombre, p.apellido
        ORDER BY totalVendido DESC
        """, nativeQuery = true)
    List<VentaVendedorDTO> obtenerReporteVentasPorVendedor();
}