package com.raidsoft.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                // Recursos estáticos públicos (CSS, JS, Imágenes)
                .requestMatchers("/css/**", "/js/**", "/images/**", "/Profiles/**").permitAll()
                // Permitir acceso público al Login y al Registro
                .requestMatchers("/login", "/register", "/redirectByRole").permitAll()
                // Rutas protegidas por Rol
                .requestMatchers("/admin/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/vendedor/**").hasRole("VENDEDOR")
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
                )
                .formLogin(login -> login
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/redirectByRole", true) // Redirige aquí si el login es correcto
                .failureUrl("/login?error") // Redirige aquí si la contraseña falla
                )
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
                );

        return http.build();
    }

    // --- BEAN OBLIGATORIO PARA BCRYPT ---
    // Este componente se encarga de encriptar en el registro y verificar en el login.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
