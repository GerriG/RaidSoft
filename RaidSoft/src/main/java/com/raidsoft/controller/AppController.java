package com.raidsoft.controller;

import com.raidsoft.model.Rol;
import com.raidsoft.model.Usuario;
import com.raidsoft.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AppController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Muestra la vista del Login (que incluye el panel de registro deslizante)
    @GetMapping("/login")
    public String login() {
        return "login"; 
    }

    // Redirección inteligente post-login según el rol
    @GetMapping("/redirectByRole")
    public String defaultAfterLogin(Authentication auth) {
        String rol = auth.getAuthorities().toString();
        if (rol.contains("ADMINISTRADOR")) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/vendedor/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminPanel() { 
        return "admin_dashboard"; 
    }

    @GetMapping("/vendedor/dashboard")
    public String vendedorPanel() { 
        return "vendedor_dashboard"; 
    }

    // --- PROCESO DE REGISTRO ---
    @PostMapping("/register")
    public String registerUser(@ModelAttribute Usuario usuario, RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Intentando registrar: " + usuario.getUsername());

            // 1. Verificar si el usuario ya existe
            if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
                redirectAttributes.addFlashAttribute("error", "El usuario '" + usuario.getUsername() + "' ya existe.");
                return "redirect:/login?error";
            }

            // 2. Encriptar la contraseña (CRUCIAL para que el Login funcione después)
            String passEncriptada = passwordEncoder.encode(usuario.getPassword());
            usuario.setPassword(passEncriptada);
            
            // 3. Configurar valores por defecto
            usuario.setEstado(true); // Usuario activo
            
            // Si no se seleccionó rol, asignar Vendedor por defecto
            if (usuario.getRol() == null) {
                usuario.setRol(Rol.VENDEDOR);
            }

            // 4. Guardar en Base de Datos
            usuarioRepository.save(usuario);
            System.out.println("Usuario registrado con éxito: " + usuario.getUsername());
            
            // 5. Mensaje de éxito
            redirectAttributes.addFlashAttribute("success", "¡Cuenta creada exitosamente! Por favor, inicia sesión.");
            return "redirect:/login";

        } catch (Exception e) {
            // Captura errores (como nombres muy largos, nulos, base de datos caída, etc.)
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al registrar: " + e.getMessage());
            return "redirect:/login?error";
        }
    }
}