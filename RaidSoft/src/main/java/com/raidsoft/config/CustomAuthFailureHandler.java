package com.raidsoft.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAuthFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        
        // Determinar el mensaje de error
        String errorMessage = "Credenciales incorrectas o usuario deshabilitado.";
        if (exception != null && exception.getMessage() != null) {
            if (exception.getMessage().contains("Bad credentials")) {
                errorMessage = "Usuario o contraseña incorrectos.";
            } else if (exception.getMessage().contains("User is disabled")) {
                 errorMessage = "Tu cuenta está deshabilitada.";
            }
        }
        
        // 1. Preparar la respuesta JSON
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("status", "error");
        responseBody.put("message", errorMessage);
        
        // 2. Escribir la respuesta
        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // HTTP 401 Unauthorized
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), responseBody);
    }
}