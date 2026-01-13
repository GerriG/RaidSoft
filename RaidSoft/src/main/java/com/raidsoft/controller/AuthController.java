package com.raidsoft.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // Muestra la página de Login (que contiene el JS para llamar a tu AuthRestController)
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // Redirige al usuario a su panel correspondiente después de loguearse
    @GetMapping("/redirectByRole")
    public String defaultAfterLogin(Authentication auth) {
        String rol = auth.getAuthorities().toString();
        if (rol.contains("ADMINISTRADOR")) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/vendedor/dashboard";
    }
}