package com.raidsoft.controller;

import com.raidsoft.dto.VentaVendedorDTO;
import com.raidsoft.model.Perfil;
import com.raidsoft.model.Usuario;
import com.raidsoft.repository.UsuarioRepository;
import com.raidsoft.service.ProductoService;
import com.raidsoft.service.VentaService;
import jakarta.validation.ConstraintViolationException;
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
    private ProductoService productoService;

    @Autowired
    private VentaService ventaService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // =======================================================
    // 1. DASHBOARD PRINCIPAL
    // =======================================================
    @GetMapping("/dashboard")
    public String adminPanel(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);

        model.addAttribute("totalProductos", productoService.contarTotalProductos());
        model.addAttribute("alertasStock", productoService.contarAlertasStock());
        model.addAttribute("stockTotal", productoService.obtenerStockTotal());

        return "admin_dashboard";
    }

    // =======================================================
    // 2. GESTIÓN DE USUARIOS
    // =======================================================
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);

        model.addAttribute("listaUsuarios", usuarioRepository.findAll());

        return "admin_usuarios";
    }

    // =======================================================
    // GUARDAR USUARIO + PERFIL (versión combinada, mejorada)
    // =======================================================
    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuarioNuevo,
            @ModelAttribute Perfil perfilNuevo,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            // 1. Validación manual: Usuario duplicado
            if (usuarioRepository.findByUsername(usuarioNuevo.getUsername()).isPresent()) {
                throw new RuntimeException("El nombre de usuario ya existe.");
            }

            // Configuración
            usuarioNuevo.setPassword(passwordEncoder.encode(usuarioNuevo.getPassword()));
            usuarioNuevo.setEstado(true);
            usuarioNuevo.setPerfil(perfilNuevo);
            perfilNuevo.setUsuario(usuarioNuevo);

            // Guardar
            usuarioRepository.save(usuarioNuevo);

            redirectAttributes.addFlashAttribute("success", "Usuario y perfil registrados correctamente.");
            return "redirect:/admin/usuarios";

        } catch (Exception e) {

            String mensajeError = "Error desconocido al registrar.";

            // ======================================================
            // BUSCAR CAUSA RAÍZ Y EXTRAER MENSAJE LIMPIO
            // ======================================================
            Throwable causa = e;
            boolean esValidacion = false;

            while (causa != null) {
                if (causa instanceof ConstraintViolationException) {
                    ConstraintViolationException cvEx = (ConstraintViolationException) causa;
                    if (!cvEx.getConstraintViolations().isEmpty()) {
                        mensajeError = cvEx.getConstraintViolations().iterator().next().getMessage();
                        esValidacion = true;
                    }
                    break;
                }
                causa = causa.getCause();
            }

            if (!esValidacion) {
                mensajeError = e.getMessage();
            }

            // Mantener datos cargados
            // Aquí seguimos usando "error" para que se abra el modal automáticamente en la vista
            model.addAttribute("error", mensajeError);
            model.addAttribute("usuarioNuevo", usuarioNuevo);
            model.addAttribute("perfilNuevo", perfilNuevo);

            // Recargar lista
            model.addAttribute("listaUsuarios", usuarioRepository.findAll());

            return "admin_usuarios";
        }
    }

    // =======================================================
    // ACTIVAR / DESACTIVAR USUARIO (CORREGIDO)
    // =======================================================
    @GetMapping("/usuarios/toggle/{id}")
    public String toggleUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Authentication auth) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario != null && usuario.getUsername().equals(auth.getName())) {
            // CAMBIO: Usamos "globalError" para evitar abrir el modal del formulario
            redirectAttributes.addFlashAttribute("globalError", "No puedes desactivar tu propia cuenta.");
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

    // =======================================================
    // ELIMINAR USUARIO (CORREGIDO)
    // =======================================================
    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Authentication auth) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario != null && usuario.getUsername().equals(auth.getName())) {
            // CAMBIO: Usamos "globalError"
            redirectAttributes.addFlashAttribute("globalError", "No puedes eliminar tu propia cuenta.");
            return "redirect:/admin/usuarios";
        }

        try {
            usuarioRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado permanentemente.");
        } catch (Exception e) {
            // CAMBIO: Usamos "globalError"
            redirectAttributes.addFlashAttribute("globalError", "No se puede eliminar: El usuario tiene historial de ventas.");
        }

        return "redirect:/admin/usuarios";
    }

    // =======================================================
    // 3. REPORTES FINANCIEROS
    // =======================================================
    @GetMapping("/ventas/reporte-vendedores")
    public String verReporteVentas(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);

        List<VentaVendedorDTO> reporte = ventaService.obtenerReporteVendedores();
        model.addAttribute("listaVendedores", reporte);

        return "admin_reporte_ventas";
    }
}