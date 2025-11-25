package com.raidsoft.service;

import com.raidsoft.dto.VentaVendedorDTO;
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

    @Transactional
    public Venta registrarVenta(Usuario vendedor, Producto producto, int cantidad) throws Exception {
        if (producto.getStock() < cantidad) {
            throw new Exception("Stock insuficiente.");
        }
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);

        Venta venta = new Venta();
        venta.setVendedor(vendedor);
        venta.setProducto(producto);
        venta.setCantidad(cantidad);
        
        // Cálculo de total (Asegúrate que tu modelo Producto tenga precioVenta como BigDecimal o Double)
        // Si en tu modelo es Double:
        venta.setTotal(producto.getPrecioVenta().doubleValue() * cantidad); 
        // Si en tu modelo es BigDecimal:
        // venta.setTotal(producto.getPrecioVenta().multiply(new BigDecimal(cantidad)));

        venta.setFecha(LocalDateTime.now());
        return ventaRepository.save(venta);
    }

    public List<Venta> historialVentas(Usuario vendedor) {
        return ventaRepository.findByVendedorOrderByFechaDesc(vendedor);
    }

    // --- MÉTODO PARA EL REPORTE DE ADMIN ---
    public List<VentaVendedorDTO> obtenerReporteVendedores() {
        return ventaRepository.obtenerReporteVentasPorVendedor();
    }
}