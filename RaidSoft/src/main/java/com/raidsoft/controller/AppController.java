package com.raidsoft.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {

    @GetMapping("/login")
    public String login() {
        return "login"; // Busca login.html
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
    public String adminPanel() { return "admin_dashboard"; } // Crea este HTML

    @GetMapping("/vendedor/dashboard")
    public String vendedorPanel() { return "vendedor_dashboard"; } // Crea este HTML
}