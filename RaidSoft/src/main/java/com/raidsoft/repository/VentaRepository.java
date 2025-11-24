package com.raidsoft.repository;

import com.raidsoft.model.Venta;
import com.raidsoft.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    List<Venta> findByVendedorOrderByFechaDesc(Usuario vendedor);
}