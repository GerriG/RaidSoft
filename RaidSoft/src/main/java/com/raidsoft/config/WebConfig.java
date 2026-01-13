package com.raidsoft.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        // --- 1. Configuración para Profiles ---
        String pathProfiles = Paths.get("src", "main", "resources", "static", "Profiles")
                                   .toAbsolutePath()
                                   .toUri()
                                   .toString();
        // FIX: Asegurar barra al final
        if (!pathProfiles.endsWith("/")) { pathProfiles += "/"; }

        // --- 2. Configuración para Productos ---
        String pathProductos = Paths.get("src", "main", "resources", "static", "Productos")
                                   .toAbsolutePath()
                                   .toUri()
                                   .toString();
        // FIX CRÍTICO: Asegurar barra al final para que Spring pueda entrar al directorio
        if (!pathProductos.endsWith("/")) { pathProductos += "/"; }

        System.out.println("------------------------------------------------------");
        System.out.println("Mapeando /Profiles/** -> " + pathProfiles);
        System.out.println("Mapeando /Productos/** -> " + pathProductos);
        System.out.println("------------------------------------------------------");

        // Registrar manejadores con prioridad a la carpeta física (src) y luego al classpath (target)
        
        registry.addResourceHandler("/Profiles/**")
                .addResourceLocations(pathProfiles, "classpath:/static/Profiles/");

        registry.addResourceHandler("/Productos/**")
                .addResourceLocations(pathProductos, "classpath:/static/Productos/");

        // 3. Recursos estáticos por defecto (CSS, JS)
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}