package com.raidsoft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        // 1. Configuración para Profiles (Ya la tenías)
        String pathProfiles = Paths.get("src", "main", "resources", "static", "Profiles")
                                   .toAbsolutePath()
                                   .toUri()
                                   .toString();

        // 2. NUEVA CONFIGURACIÓN PARA PRODUCTOS
        String pathProductos = Paths.get("src", "main", "resources", "static", "Productos")
                                   .toAbsolutePath()
                                   .toUri()
                                   .toString();

        System.out.println("------------------------------------------------------");
        System.out.println("Mapeando /Profiles/** -> " + pathProfiles);
        System.out.println("Mapeando /Productos/** -> " + pathProductos);
        System.out.println("------------------------------------------------------");

        // Registrar manejador para Profiles
        registry.addResourceHandler("/Profiles/**")
                .addResourceLocations(pathProfiles);

        // Registrar manejador para Productos (ESTO FALTABA)
        registry.addResourceHandler("/Productos/**")
                .addResourceLocations(pathProductos);

        // 3. Recursos estáticos por defecto (CSS, JS)
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}