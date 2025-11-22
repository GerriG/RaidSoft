package com.raidsoft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Convertimos la ruta a URI para que funcione en Windows/Linux/Mac sin errores
        String pathProfiles = Paths.get("src", "main", "resources", "static", "Profiles")
                                   .toAbsolutePath()
                                   .toUri()
                                   .toString();

        // Imprimimos en consola para verificar que la ruta es correcta al arrancar
        System.out.println("------------------------------------------------------");
        System.out.println("Mapeando recursos /Profiles/** a: " + pathProfiles);
        System.out.println("------------------------------------------------------");

        registry.addResourceHandler("/Profiles/**")
                .addResourceLocations(pathProfiles);
    }
}