package com.raidsoft.controller;

import com.raidsoft.model.Producto;
import com.raidsoft.model.Usuario;
import com.raidsoft.repository.ProductoRepository;
import com.raidsoft.repository.UsuarioRepository;
import com.raidsoft.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class ProductoController {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/productos")
    public String listarProductos(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);
        model.addAttribute("listaProductos", productoRepository.findAll());
        return "admin_productos";
    }

    @GetMapping("/stock")
    public String reporteStock(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);
        try {
            model.addAttribute("productosFaltantes", productoRepository.findProductosByStockCritico());
        } catch (Exception e) {
            model.addAttribute("productosFaltantes", productoRepository.findAll());
        }
        return "admin_stock";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProducto(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);
        model.addAttribute("producto", new Producto());
        model.addAttribute("accion", "Crear");
        return "form_producto";
    }

    @GetMapping("/productos/editar/{id}")
    public String editarProducto(@PathVariable("id") Long id, Model model, Authentication auth, RedirectAttributes attr) {
        Producto p = productoRepository.findById(id).orElse(null);
        if (p == null) {
            attr.addFlashAttribute("error", "Producto no encontrado");
            return "redirect:/admin/productos";
        }
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);
        model.addAttribute("producto", p);
        model.addAttribute("accion", "Editar");
        return "form_producto";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(@ModelAttribute Producto producto, @RequestParam("file") MultipartFile imagen, RedirectAttributes attr) {
        try {
            boolean isNew = producto.getIdProducto() == null;
            productoService.guardarProducto(producto, imagen);
            attr.addFlashAttribute("success", isNew ? "Creado exitosamente." : "Actualizado exitosamente.");
        } catch (Exception e) {
            attr.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/admin/productos";
    }

    @GetMapping("/productos/toggle/{id}")
    public String toggleProducto(@PathVariable("id") Long id, RedirectAttributes attr) {
        Producto p = productoRepository.findById(id).orElse(null);
        if (p != null) {
            p.setEstado(!p.getEstado());
            productoRepository.save(p);
            attr.addFlashAttribute("success", "Estado actualizado.");
        }
        return "redirect:/admin/productos";
    }

    @GetMapping("/productos/eliminar/{id}")
    public String eliminarProducto(@PathVariable("id") Long id, RedirectAttributes attr) {
        try {
            productoRepository.deleteById(id);
            attr.addFlashAttribute("success", "Eliminado.");
        } catch (Exception e) {
            attr.addFlashAttribute("error", "Error al eliminar.");
        }
        return "redirect:/admin/productos";
    }
}