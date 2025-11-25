package com.raidsoft.controller;

import com.raidsoft.model.Producto;
import com.raidsoft.model.Usuario;
import com.raidsoft.model.Venta;
import com.raidsoft.repository.ProductoRepository;
import com.raidsoft.repository.UsuarioRepository;
import com.raidsoft.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/vendedor")
public class VendedorController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private VentaService ventaService;

    // --- DASHBOARD ---
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);
        return "vendedor_dashboard";
    }

    // --- CONSULTA STOCK ---
    @GetMapping("/stock")
    public String stock(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);
        model.addAttribute("productos", productoRepository.findAll());
        return "vendedor_stock";
    }

    @GetMapping("/stock/search")
    public String buscarStock(@RequestParam("query") String query, Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);
        
        List<Producto> productos = (query == null || query.trim().isEmpty()) 
            ? productoRepository.findAll() 
            : productoRepository.search(query);
            
        model.addAttribute("productos", productos);
        model.addAttribute("query", query);
        return "vendedor_stock";
    }

    @GetMapping("/productos")
    public String pos(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);
        
        List<Producto> productos = productoRepository.findAll().stream()
            .filter(p -> p != null && Boolean.TRUE.equals(p.getEstado()) && p.getStock() > 0)
            .toList();
            
        model.addAttribute("productos", productos);
        
        return "vender_producto"; 
    }

    @PostMapping("/vender/{id}")
    public String procesarVenta(@PathVariable("id") Long idProducto, 
                                @RequestParam("cantidad") int cantidad, 
                                RedirectAttributes attr, 
                                Authentication auth) {
        
        Usuario vendedor = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        Producto producto = productoRepository.findById(idProducto).orElse(null);

        if (producto == null) {
            attr.addFlashAttribute("error", "Producto no encontrado.");
            return "redirect:/vendedor/productos";
        }
        
        if (cantidad <= 0) {
            attr.addFlashAttribute("error", "La cantidad debe ser mayor a 0.");
            return "redirect:/vendedor/productos";
        }

        if (cantidad > producto.getStock()) {
            attr.addFlashAttribute("error", "Stock insuficiente. Disponible: " + producto.getStock());
            return "redirect:/vendedor/productos";
        }

        try {
            ventaService.registrarVenta(vendedor, producto, cantidad);
            attr.addFlashAttribute("success", "Venta registrada correctamente.");
        } catch (Exception e) {
            attr.addFlashAttribute("error", "Error al procesar venta: " + e.getMessage());
        }
        
        return "redirect:/vendedor/productos";
    }

    // --- HISTORIAL ---
    @GetMapping("/ventas")
    public String historial(Model model, Authentication auth) {
        Usuario vendedor = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        List<Venta> ventas = ventaService.historialVentas(vendedor);
        
        model.addAttribute("usuario", vendedor);
        model.addAttribute("ventas", ventas);
        
        return "ventas_realizadas";
    }
}