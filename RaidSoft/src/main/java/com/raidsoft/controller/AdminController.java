package com.raidsoft.controller;

import com.raidsoft.dto.VentaVendedorDTO;
import com.raidsoft.model.Usuario;
import com.raidsoft.repository.UsuarioRepository;
import com.raidsoft.service.ProductoService;
import com.raidsoft.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoService productoService; // Para contadores del dashboard

    @Autowired
    private VentaService ventaService; // Para el reporte de ventas

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =======================================================
    // 1. DASHBOARD PRINCIPAL
    // =======================================================
    @GetMapping("/dashboard")
    public String adminPanel(Model model, Authentication auth) {
        // Usuario para sidebar
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);

        // Estadísticas en Tiempo Real
        model.addAttribute("totalProductos", productoService.contarTotalProductos());
        model.addAttribute("alertasStock", productoService.contarAlertasStock());
        model.addAttribute("stockTotal", productoService.obtenerStockTotal());

        return "admin_dashboard";
    }

    // =======================================================
    // 2. GESTIÓN DE USUARIOS (Equipo)
    // =======================================================
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);
        
        // Lista completa para la tabla
        model.addAttribute("listaUsuarios", usuarioRepository.findAll());
        
        return "admin_usuarios"; // Contiene el Modal de creación
    }

    // Guardar Usuario (Funciona con el Modal)
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuarioNuevo, RedirectAttributes redirectAttributes) {
        try {
            // Validación: Usuario duplicado
            if (usuarioRepository.findByUsername(usuarioNuevo.getUsername()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya existe.");
                return "redirect:/admin/usuarios";
            }
            
            // Configuración por defecto
            usuarioNuevo.setPassword(passwordEncoder.encode(usuarioNuevo.getPassword()));
            usuarioNuevo.setEstado(true); // Activo al crear
            
            usuarioRepository.save(usuarioNuevo);
            
            redirectAttributes.addFlashAttribute("success", "Usuario registrado correctamente.");
            return "redirect:/admin/usuarios";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }

    // Bloquear / Desbloquear Usuario
    @GetMapping("/usuarios/toggle/{id}")
    public String toggleUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Authentication auth) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        
        // Seguridad: No bloquearse a uno mismo
        if (usuario != null && usuario.getUsername().equals(auth.getName())) {
            redirectAttributes.addFlashAttribute("error", "No puedes desactivar tu propia cuenta.");
            return "redirect:/admin/usuarios";
        }
        
        if (usuario != null) {
            usuario.setEstado(!usuario.getEstado());
            usuarioRepository.save(usuario);
            String estado = usuario.getEstado() ? "activado" : "desactivado";
            redirectAttributes.addFlashAttribute("success", "Usuario " + estado + " correctamente.");
        }
        return "redirect:/admin/usuarios";
    }

    // Eliminar Usuario
    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Authentication auth) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        
        // Seguridad: No eliminarse a uno mismo
        if (usuario != null && usuario.getUsername().equals(auth.getName())) {
            redirectAttributes.addFlashAttribute("error", "No puedes eliminar tu propia cuenta.");
            return "redirect:/admin/usuarios";
        }
        
        try {
            usuarioRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado permanentemente.");
        } catch (Exception e) {
            // Error común: Integridad referencial (tiene ventas asociadas)
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar: El usuario tiene historial de ventas.");
        }
        return "redirect:/admin/usuarios";
    }

    // =======================================================
    // 3. REPORTES FINANCIEROS
    // =======================================================
    @GetMapping("/ventas/reporte-vendedores")
    public String verReporteVentas(Model model, Authentication auth) {
        // Usuario para sidebar
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);

        // Obtener datos del Ranking (DTO)
        List<VentaVendedorDTO> reporte = ventaService.obtenerReporteVendedores();
        model.addAttribute("listaVendedores", reporte);

        return "admin_reporte_ventas";
    }
    
    // Nota: La gestión de Productos y Stock está en ProductoController.java
}