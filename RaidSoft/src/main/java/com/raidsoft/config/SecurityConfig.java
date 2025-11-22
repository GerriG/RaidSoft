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

    // Ahora ambos beans se encuentran correctamente
    @Autowired
    private AuthenticationSuccessHandler customAuthSuccessHandler;
    
    @Autowired
    private AuthenticationFailureHandler customAuthFailureHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Rutas públicas
                .requestMatchers("/css/**", "/Profiles/**", "/register", "/login", "/").permitAll()
                .requestMatchers("/api/register").permitAll() // Necesario para el endpoint de registro JSON
                
                // Rutas privadas con roles
                .requestMatchers("/admin/**").hasAuthority("ADMINISTRADOR")
                .requestMatchers("/vendedor/**").hasAnyAuthority("VENDEDOR", "ADMINISTRADOR")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") 
                .loginProcessingUrl("/login") // Endpoint que procesa el login
                // Configuramos los handlers JSON para las respuestas de AJAX
                .successHandler(customAuthSuccessHandler)
                .failureHandler(customAuthFailureHandler)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // Deshabilitar CSRF para permitir peticiones POST via AJAX sin token CSRF
            .csrf(csrf -> csrf.disable()); 

        return http.build();
    }

    // --- BEAN OBLIGATORIO PARA BCRYPT ---
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}