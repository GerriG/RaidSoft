package com.raidsoft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        String pathProfiles = Paths.get("src", "main", "resources", "static", "Profiles")
                                   .toAbsolutePath()
                                   .toUri()
                                   .toString();

        System.out.println("------------------------------------------------------");
        System.out.println("Mapeando recursos /Profiles/** a: " + pathProfiles);
        System.out.println("------------------------------------------------------");

        // 1. Mapeo para el recurso custom (/Profiles/**)
        registry.addResourceHandler("/Profiles/**")
                .addResourceLocations(pathProfiles);

        // 2. CORRECCIÓN CRÍTICA: Volver a añadir el manejador de recursos estáticos por defecto.
        // Esto sirve los archivos CSS, JS, etc. desde la carpeta /static/
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/"); //
    }
}