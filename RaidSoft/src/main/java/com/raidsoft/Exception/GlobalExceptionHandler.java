package com.raidsoft.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Error por archivo muy pesado
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxSizeException(MaxUploadSizeExceededException exc, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "El archivo es demasiado grande. El límite es 10MB.");
        return "redirect:/admin/productos";
    }

    // 2. Errores inesperados del sistema (Catch-All)
    // Esto captura cualquier cosa que se nos haya escapado en los controladores
    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception exc, RedirectAttributes redirectAttributes) {
        exc.printStackTrace(); // Para verlo en consola
        redirectAttributes.addFlashAttribute("error", "Ocurrió un error inesperado: " + exc.getMessage());
        
        // Redirigimos a una ruta segura (puedes cambiarlo si quieres)
        return "redirect:/admin/productos";
    }
}