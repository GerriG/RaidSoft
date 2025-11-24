package com.raidsoft.service;

import com.raidsoft.model.Producto;
import com.raidsoft.model.Usuario;
import com.raidsoft.model.Venta;
import com.raidsoft.repository.ProductoRepository;
import com.raidsoft.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    // Registro de venta con validación de stock y actualización automática
    @Transactional
    public Venta registrarVenta(Usuario vendedor, Producto producto, int cantidad) throws Exception {
        // 1. Validación de stock
        if (producto.getStock() < cantidad) {
            throw new Exception("No hay suficiente stock para este producto.");
        }

        // 2. Actualizar stock
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);

        // 3. Crear venta
        Venta venta = new Venta();
        venta.setVendedor(vendedor);
        venta.setProducto(producto);
        venta.setCantidad(cantidad);

        // 4. Calcular total
        // Asumiendo que getPrecioVenta() retorna BigDecimal. Si es Double, elimina el BigDecimal.valueOf
        venta.setTotal(producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad)).doubleValue());

        // 5. Registrar fecha
        venta.setFecha(LocalDateTime.now());

        // 6. Guardar y retornar
        return ventaRepository.save(venta);
    }

    // Listar todas las ventas
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    // Listar ventas de un vendedor específico
    public List<Venta> listarVentasPorVendedor(Usuario vendedor) {
        return ventaRepository.findByVendedorOrderByFechaDesc(vendedor);
    }

    public List<Venta> historialVentas(Usuario vendedor) {
        return listarVentasPorVendedor(vendedor);
    }
}