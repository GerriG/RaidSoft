package com.raidsoft.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private AuthenticationSuccessHandler customAuthSuccessHandler;
    
    @Autowired
    private AuthenticationFailureHandler customAuthFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                
                // --- EXCLUSIÓN CRÍTICA DE ARCHIVOS ESTÁTICOS (SOLUCIÓN AL ERROR MIME TYPE) ---
                .requestMatchers(
                    "/css/**", "/js/**", "/images/**", 
                    "/Profiles/**", "/Productos/**", "/favicon.ico"
                ).permitAll()
                
                // --- RUTAS PÚBLICAS Y AJAX DE AUTENTICACIÓN/RECUPERACIÓN ---
                .requestMatchers(
                    "/login", "/", "/register", "/api/register", 
                    "/api/reset-password", "/reset-password"
                ).permitAll()
                
                // Rutas privadas con roles
                .requestMatchers("/admin/**").hasAuthority("ADMINISTRADOR")
                .requestMatchers("/vendedor/**").hasAnyAuthority("VENDEDOR", "ADMINISTRADOR")
                .requestMatchers("/perfil/**").authenticated() 
                
                // Cualquier otra solicitud requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(customAuthSuccessHandler)
                .failureHandler(customAuthFailureHandler)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // Deshabilitamos CSRF para permitir peticiones POST (AJAX Login/Reset) sin el token
            .csrf(csrf -> csrf.disable()); 

        return http.build();
    }

    // --- BEAN OBLIGATORIO PARA BCRYPT ---
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}