package com.raidsoft.controller;

import com.raidsoft.model.Perfil;
import com.raidsoft.model.Usuario;
import com.raidsoft.repository.UsuarioRepository;
import com.raidsoft.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    // 1. RUTA PARA VER EL PERFIL (GET /perfil)
    @GetMapping
    public String verPerfil(Authentication auth, Model model) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);
        // Si el perfil es null, enviamos uno vacío para que no rompa el HTML
        model.addAttribute("perfil", usuario.getPerfil() != null ? usuario.getPerfil() : new Perfil());
        
        return "perfil";
    }

    // 2. RUTA PARA EDITAR EL PERFIL (GET /perfil/editar)
    @GetMapping("/editar")
    public String editarPerfil(Authentication auth, Model model) {
        Usuario usuario = usuarioRepository.findByUsername(auth.getName()).orElse(null);
        if (usuario == null) return "redirect:/login";

        model.addAttribute("usuario", usuario);
        model.addAttribute("perfil", usuario.getPerfil() != null ? usuario.getPerfil() : new Perfil());
        
        return "edit_perfil";
    }

    // 3. RUTA PARA GUARDAR LOS CAMBIOS (POST /perfil/guardar)
    @PostMapping("/guardar")
    public String guardarPerfil(
            Authentication auth,
            @ModelAttribute Perfil perfilForm,
            @RequestParam(value = "file", required = false) MultipartFile archivo,
            @RequestParam(value = "newPassword", required = false) String newPassword,
            RedirectAttributes attr) {
        try {
            // Buscamos el usuario antes de la actualización
            Usuario usuarioActual = usuarioRepository.findByUsername(auth.getName()).orElseThrow();
            
            // Llama al servicio para ejecutar la lógica de guardado
            usuarioService.actualizarPerfil(usuarioActual, perfilForm, archivo, newPassword);
            
            attr.addFlashAttribute("success", "Perfil actualizado correctamente.");
            return "redirect:/perfil";

        } catch (Exception e) {
            e.printStackTrace();
            attr.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
            return "redirect:/perfil/editar";
        }
    }
}