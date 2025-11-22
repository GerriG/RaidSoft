package com.raidsoft.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component; // Importación necesaria

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component // <-- ¡ESTO RESUELVE EL ERROR DE "UNSATISFIED DEPENDENCY"!
public class CustomAuthSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 1. Determinar URL de redirección basada en el rol
        String redirectUrl = "/";
        String rol = authentication.getAuthorities().toString();

        if (rol.contains("ADMINISTRADOR")) {
            redirectUrl = "/admin/dashboard";
        } else if (rol.contains("VENDEDOR")) {
            redirectUrl = "/vendedor/dashboard";
        }

        // 2. Preparar la respuesta JSON
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("status", "success");
        responseBody.put("message", "Login exitoso.");
        responseBody.put("redirectUrl", redirectUrl);

        // 3. Escribir la respuesta
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), responseBody);
    }
}
