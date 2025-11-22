package com.raidsoft.controller;

import com.raidsoft.repository.ProductoRepository;
import com.raidsoft.model.Rol;
import com.raidsoft.model.Usuario;
import com.raidsoft.repository.UsuarioRepository;
import com.raidsoft.service.UsuarioService; // Importar el servicio nuevo
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AppController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UsuarioService usuarioService; // Inyección del servicio
    
    // Inyectar el repositorio de productos
    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    @GetMapping("/redirectByRole")
    public String defaultAfterLogin(Authentication auth) {
        String rol = auth.getAuthorities().toString();
        if (rol.contains("ADMINISTRADOR")) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/vendedor/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminPanel(Model model, Authentication auth) { 
        // Pasamos datos del usuario al dashboard para mostrar nombre/foto
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuario);
        return "admin_dashboard"; 
    }

    @GetMapping("/vendedor/dashboard")
    public String vendedorPanel(Model model, Authentication auth) { 
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuario);
        return "vendedor_dashboard"; 
    }

    // --- GESTIÓN DE PERFIL (NUEVO) ---

    // 1. Ver Perfil (Solo Lectura)
    @GetMapping("/perfil")
    public String verPerfil(Authentication auth, Model model) {
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        model.addAttribute("usuario", usuario);
        return "perfil"; 
    }

    // 2. Editar Perfil (Formulario)
    @GetMapping("/perfil/editar")
    public String editarPerfil(Authentication auth, Model model) {
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElseThrow();
        model.addAttribute("usuario", usuario);
        return "edit_perfil";
    }
    
    // Agregar método para la vista de productos
    @GetMapping("/admin/productos")
    public String adminProductos(Model model, Authentication auth) {
        // Datos del usuario para el sidebar/header
        String username = auth.getName();
        Usuario usuario = usuarioRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuario);

        // Lista de productos para la tabla
        model.addAttribute("listaProductos", productoRepository.findAll());
        
        return "admin_productos";
    }

    // 3. Procesar Cambios
    @PostMapping("/perfil/guardar")
    public String guardarPerfil(Authentication auth,
                                @ModelAttribute Usuario usuarioForm,
                                @RequestParam("file") MultipartFile archivo,
                                @RequestParam(required = false) String newPassword,
                                RedirectAttributes redirectAttributes) {
        try {
            String username = auth.getName();
            Usuario usuarioActual = usuarioRepository.findByUsername(username).orElseThrow();
            
            usuarioService.actualizarPerfil(usuarioActual, usuarioForm, archivo, newPassword);

            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente.");
            return "redirect:/perfil";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar perfil: " + e.getMessage());
            return "redirect:/perfil/editar";
        }
    }

    // --- REGISTRO ---
    @PostMapping("/register")
    public String registerUser(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "El usuario ya existe.");
                return "redirect:/login?error";
            }
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            usuario.setEstado(true);
            if (usuario.getRol() == null) usuario.setRol(Rol.VENDEDOR);
            
            usuarioRepository.save(usuario);
            redirectAttributes.addFlashAttribute("success", "Cuenta creada. Inicia sesión.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/login?error";
        }
    }
}