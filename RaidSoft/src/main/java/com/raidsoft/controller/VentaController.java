package com.raidsoft.controller;

import com.raidsoft.dto.VentaVendedorDTO;
import com.raidsoft.service.PdfService;
import com.raidsoft.service.VentaService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private PdfService pdfService;

    // ==========================================
    // VISTA HTML DEL REPORTE DE VENDEDORES
    // ==========================================
    @GetMapping("/reporte-vendedores")
    public String verReporteVendedores(Model model) {
        List<VentaVendedorDTO> reporte = ventaService.obtenerReporteVendedores();
        model.addAttribute("listaVendedores", reporte);
        return "admin_reporte_ventas";
    }

    // ==========================================
    // DESCARGAR REPORTE PDF
    // URL: localhost:8080/ventas/reporte-vendedores/pdf
    // ==========================================
    @GetMapping("/reporte-vendedores/pdf")
    public void descargarReportePdf(HttpServletResponse response) throws IOException {
        List<VentaVendedorDTO> reporte = ventaService.obtenerReporteVendedores();

        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "inline; filename=Ranking_Vendedores_RaidSoft.pdf";
        response.setHeader(headerKey, headerValue);

        pdfService.generarReporteRanking(response, reporte);
    }
}