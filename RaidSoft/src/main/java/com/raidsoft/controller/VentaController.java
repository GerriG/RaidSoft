package com.raidsoft.controller;

import com.raidsoft.dto.VentaVendedorDTO;
import com.raidsoft.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/ventas") // Todas las URLs de este archivo empezarán con /ventas
public class VentaController {

    @Autowired
    private VentaService ventaService;

    // ==========================================
    // VISTA: REPORTE DE VENDEDORES
    // URL: localhost:8080/ventas/reporte-vendedores
    // ==========================================
   @GetMapping("/reporte-vendedores")
    public String verReporteVendedores(Model model) {
        List<VentaVendedorDTO> reporte = ventaService.obtenerReporteVendedores();
        model.addAttribute("listaVendedores", reporte);
        
        // CAMBIO: Quitamos "ventas/" y usamos tu convención de nombres
        return "admin_reporte_ventas"; 
    }
}