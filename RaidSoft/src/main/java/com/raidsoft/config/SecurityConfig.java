package com.raidsoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll() // Recursos estáticos
                .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/vendedor/**").hasRole("VENDEDOR")
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login") // Usaremos tu diseño personalizado
                .permitAll()
                .defaultSuccessUrl("/redirectByRole", true) // Redirección inteligente
            )
            .logout(logout -> logout.permitAll());
        
        return http.build();
    }
}