package com.raidsoft.controller;

import com.raidsoft.dto.VentaVendedorDTO;
import com.raidsoft.model.Perfil;
import com.raidsoft.model.Usuario;
import com.raidsoft.model.Producto;
import com.raidsoft.repository.UsuarioRepository;
import com.raidsoft.repository.ProductoRepository;
import com.raidsoft.service.ProductoService;
import com.raidsoft.service.VentaService;
import com.raidsoft.service.PdfService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
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
    private ProductoRepository productoRepository;

    @Autowired
    private PdfService pdfService;

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
    // 2. Gestión de Usuarios
    // =======================================================
    @GetMapping("/usuarios")
    public String listarUsuarios(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);

        model.addAttribute("listaUsuarios", usuarioRepository.findAll());

        return "admin_usuarios";
    }

    @PostMapping("/usuarios/guardar")
    public String guardarUsuario(@ModelAttribute Usuario usuarioNuevo,
            @ModelAttribute Perfil perfilNuevo,
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            if (usuarioRepository.findByUsername(usuarioNuevo.getUsername()).isPresent()) {
                throw new RuntimeException("El nombre de usuario ya existe.");
            }

            usuarioNuevo.setPassword(passwordEncoder.encode(usuarioNuevo.getPassword()));
            usuarioNuevo.setEstado(true);
            usuarioNuevo.setPerfil(perfilNuevo);
            perfilNuevo.setUsuario(usuarioNuevo);

            usuarioRepository.save(usuarioNuevo);

            redirectAttributes.addFlashAttribute("success", "Usuario y perfil registrados correctamente.");
            return "redirect:/admin/usuarios";

        } catch (Exception e) {
            String mensajeError = "Error desconocido al registrar.";

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

            model.addAttribute("error", mensajeError);
            model.addAttribute("usuarioNuevo", usuarioNuevo);
            model.addAttribute("perfilNuevo", perfilNuevo);
            model.addAttribute("listaUsuarios", usuarioRepository.findAll());

            return "admin_usuarios";
        }
    }

    // ACTIVAR / DESACTIVAR USUARIO
    @GetMapping("/usuarios/toggle/{id}")
    public String toggleUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Authentication auth) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario != null && usuario.getUsername().equals(auth.getName())) {
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

    // ELIMINAR USUARIO
    @GetMapping("/usuarios/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Long id, RedirectAttributes redirectAttributes, Authentication auth) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario != null && usuario.getUsername().equals(auth.getName())) {
            redirectAttributes.addFlashAttribute("globalError", "No puedes eliminar tu propia cuenta.");
            return "redirect:/admin/usuarios";
        }

        try {
            usuarioRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado permanentemente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("globalError", "No se puede eliminar: El usuario tiene historial de ventas.");
        }

        return "redirect:/admin/usuarios";
    }

    // =======================================================
    // 3. Reporte de Ventas por Vendedor (HTML)
    // =======================================================
    @GetMapping("/ventas/reporte-vendedores")
    public String verReporteVentas(Model model, Authentication auth) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        model.addAttribute("usuario", usuario);

        List<VentaVendedorDTO> reporte = ventaService.obtenerReporteVendedores();
        model.addAttribute("listaVendedores", reporte);

        return "admin_reporte_ventas";
    }

    // =======================================================
    // 4. PDF: Reporte de Productos
    // =======================================================
    @GetMapping("/productos/pdf")
    public void descargarReporteProductos(HttpServletResponse response) throws IOException {

        List<Producto> productos = productoRepository.findAll();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=Inventario_RaidSoft.pdf");

        pdfService.generarReporteProductos(response, productos);
    }

    // =======================================================
    // 5. PDF: Reporte de Usuarios
    // =======================================================
    @GetMapping("/usuarios/pdf")
    public void descargarReporteUsuarios(HttpServletResponse response) throws IOException {

        List<Usuario> usuarios = usuarioRepository.findAll();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=Usuarios_RaidSoft.pdf");

        pdfService.generarReporteUsuarios(response, usuarios);
    }

    // =======================================================
    // 6. PDF: Reporte de Reabastecimiento (NUEVO)
    // =======================================================
    @GetMapping("/stock/reabastecimiento/pdf")
    public void descargarReporteReabastecimiento(HttpServletResponse response) throws IOException {

        List<Producto> productosBajos = productoRepository.encontrarProductosParaReabastecer();

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "inline; filename=Orden_Reabastecimiento_RaidSoft.pdf";
        response.setHeader(headerKey, headerValue);

        pdfService.generarReporteReabastecimiento(response, productosBajos);
    }
}