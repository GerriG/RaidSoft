package com.raidsoft.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.raidsoft.model.Producto;
import com.raidsoft.model.Venta;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class PdfService {

    public void generarReciboVenta(HttpServletResponse response, Venta venta) throws IOException {
        Document document = new Document(PageSize.A5);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.GRAY);
        Font fontCuerpo = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

        Paragraph titulo = new Paragraph("RaidSoft - Comprobante de Venta", fontTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titulo);

        Paragraph fecha = new Paragraph(
                "Fecha de emisión: " + venta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                fontCuerpo
        );
        fecha.setAlignment(Paragraph.ALIGN_CENTER);
        fecha.setSpacingAfter(20);
        document.add(fecha);

        PdfPTable tablaInfo = new PdfPTable(1);
        tablaInfo.setWidthPercentage(100);

        PdfPCell celdaVendedor = new PdfPCell(
                new Phrase("Atendido por: " + venta.getVendedor().getUsername(), fontSubtitulo)
        );
        celdaVendedor.setBorder(Rectangle.NO_BORDER);
        celdaVendedor.setPaddingBottom(10);
        tablaInfo.addCell(celdaVendedor);

        document.add(tablaInfo);

        PdfPTable tablaProductos = new PdfPTable(4);
        tablaProductos.setWidthPercentage(100);
        tablaProductos.setSpacingBefore(10);
        tablaProductos.setWidths(new float[]{4f, 1.5f, 2f, 2f});

        agregarCeldaEncabezado(tablaProductos, "Producto");
        agregarCeldaEncabezado(tablaProductos, "Cant.");
        agregarCeldaEncabezado(tablaProductos, "Precio Unit.");
        agregarCeldaEncabezado(tablaProductos, "Subtotal");

        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(Locale.US);

        tablaProductos.addCell(new Phrase(venta.getProducto().getNombre(), fontCuerpo));
        tablaProductos.addCell(new Phrase(String.valueOf(venta.getCantidad()), fontCuerpo));
        tablaProductos.addCell(new Phrase(formatoMoneda.format(venta.getProducto().getPrecioVenta()), fontCuerpo));
        tablaProductos.addCell(new Phrase(formatoMoneda.format(venta.getTotal()), fontCuerpo));

        document.add(tablaProductos);

        Paragraph total = new Paragraph("TOTAL PAGADO: " + formatoMoneda.format(venta.getTotal()), fontTitulo);
        total.setAlignment(Paragraph.ALIGN_RIGHT);
        total.setSpacingBefore(20);
        document.add(total);

        Paragraph footer = new Paragraph("Gracias por su compra en RaidSoft", fontSubtitulo);
        footer.setAlignment(Paragraph.ALIGN_CENTER);
        footer.setSpacingBefore(30);
        document.add(footer);

        document.close();
    }

    private void agregarCeldaEncabezado(PdfPTable tabla, String texto) {
        PdfPCell celda = new PdfPCell(
                new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE))
        );
        celda.setBackgroundColor(Color.DARK_GRAY);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setPadding(5);
        tabla.addCell(celda);
    }

    // -------------------------------------------------------------
    // Reporte Ranking de vendedores
    // -------------------------------------------------------------
    public void generarReporteRanking(HttpServletResponse response, List<com.raidsoft.dto.VentaVendedorDTO> ranking) throws IOException {

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.BLACK);
        Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.DARK_GRAY);

        Paragraph titulo = new Paragraph("RaidSoft - Ranking de Ventas por Vendedor", fontTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titulo);

        Paragraph fecha = new Paragraph(
                "Generado el: " + java.time.LocalDateTime.now()
                        .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                fontData
        );
        fecha.setAlignment(Paragraph.ALIGN_CENTER);
        fecha.setSpacingAfter(20);
        document.add(fecha);

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 3f, 4f, 2f, 3f});

        String[] headers = {"#", "Usuario", "Nombre Completo", "Ventas", "Total Vendido"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
            cell.setBackgroundColor(Color.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.US);
        BigDecimal granTotal = BigDecimal.ZERO;
        int totalVentas = 0;
        int rank = 1;

        for (com.raidsoft.dto.VentaVendedorDTO vendedor : ranking) {

            PdfPCell cellRank = new PdfPCell(new Phrase(String.valueOf(rank++), fontData));
            cellRank.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellRank);

            table.addCell(new Phrase(vendedor.getUsername(), fontData));
            table.addCell(new Phrase(vendedor.getNombreCompleto(), fontData));

            PdfPCell cellCant = new PdfPCell(new Phrase(String.valueOf(vendedor.getCantidadVentas()), fontData));
            cellCant.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cellCant);

            PdfPCell cellTotal = new PdfPCell(new Phrase(currencyFormat.format(vendedor.getTotalVendido()), fontData));
            cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cellTotal);

            granTotal = granTotal.add(vendedor.getTotalVendido());
            totalVentas += vendedor.getCantidadVentas();
        }

        document.add(table);

        Paragraph resumen = new Paragraph("\nRESUMEN GENERAL", fontTotal);
        resumen.setSpacingBefore(10);
        document.add(resumen);

        PdfPTable tablaResumen = new PdfPTable(2);
        tablaResumen.setWidthPercentage(40);
        tablaResumen.setHorizontalAlignment(Element.ALIGN_LEFT);

        tablaResumen.addCell(new Phrase("Total Transacciones:", fontData));
        tablaResumen.addCell(new Phrase(String.valueOf(totalVentas), fontData));

        tablaResumen.addCell(new Phrase("Ingresos Totales:", fontTotal));
        tablaResumen.addCell(new Phrase(currencyFormat.format(granTotal), fontTotal));

        document.add(tablaResumen);

        document.close();
    }

    // -------------------------------------------------------------
    // Reporte de productos/inventario (método agregado)
    // -------------------------------------------------------------
    public void generarReporteProductos(HttpServletResponse response, List<Producto> productos) throws IOException {

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font fontTotales = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.DARK_GRAY);

        Paragraph titulo = new Paragraph("RaidSoft - Reporte de Inventario de Productos", fontTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titulo);

        Paragraph fecha = new Paragraph("Generado el: "
                + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), fontData);
        fecha.setAlignment(Paragraph.ALIGN_CENTER);
        fecha.setSpacingAfter(20);
        document.add(fecha);

        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2.5f, 5f, 2f, 2f, 1.5f, 2.5f});

        String[] headers = {"ID", "Código", "Producto", "P. Costo", "P. Venta", "Stock", "Valor Inv."};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
            cell.setBackgroundColor(Color.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }

        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);
        BigDecimal totalValorInventario = BigDecimal.ZERO;
        int totalItems = 0;

        for (Producto p : productos) {
            table.addCell(new Phrase(String.valueOf(p.getIdProducto()), fontData));
            table.addCell(new Phrase(p.getCodigoBarras(), fontData));
            table.addCell(new Phrase(p.getNombre(), fontData));

            table.addCell(new Phrase(currency.format(p.getPrecioCompra()), fontData));
            table.addCell(new Phrase(currency.format(p.getPrecioVenta()), fontData));

            PdfPCell celdaStock = new PdfPCell(new Phrase(String.valueOf(p.getStock()), fontData));
            celdaStock.setHorizontalAlignment(Element.ALIGN_CENTER);
            if (p.getStock() <= p.getStockMinimo()) {
                celdaStock.setBackgroundColor(new Color(255, 230, 230));
            }
            table.addCell(celdaStock);

            BigDecimal valorStock = p.getPrecioCompra().multiply(new BigDecimal(p.getStock()));
            PdfPCell celdaValor = new PdfPCell(new Phrase(currency.format(valorStock), fontData));
            celdaValor.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(celdaValor);

            totalValorInventario = totalValorInventario.add(valorStock);
            totalItems += p.getStock();
        }

        document.add(table);

        Paragraph resumenInv = new Paragraph("\nRESUMEN DE VALORACIÓN", fontTitulo);
        resumenInv.setSpacingBefore(15);
        document.add(resumenInv);

        PdfPTable tablaResumen = new PdfPTable(2);
        tablaResumen.setWidthPercentage(40);
        tablaResumen.setHorizontalAlignment(Element.ALIGN_LEFT);

        tablaResumen.addCell(new Phrase("Total Unidades en Almacén:", fontData));
        tablaResumen.addCell(new Phrase(String.valueOf(totalItems), fontTotales));

        tablaResumen.addCell(new Phrase("Valor Total Inventario (Costo):", fontData));
        tablaResumen.addCell(new Phrase(currency.format(totalValorInventario), fontTotales));

        document.add(tablaResumen);

        document.close();
    }

    // Reporte de Usuarios
    public void generarReporteUsuarios(HttpServletResponse response, List<com.raidsoft.model.Usuario> usuarios) throws IOException {
        // 1. Configuración (Horizontal recomendado para datos de usuarios)
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // 2. Fuentes
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Color.WHITE);
        Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);

        // 3. Título
        Paragraph titulo = new Paragraph("RaidSoft - Listado de Usuarios y Accesos", fontTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titulo);

        Paragraph fecha = new Paragraph("Generado el: "
                + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), fontData);
        fecha.setAlignment(Paragraph.ALIGN_CENTER);
        fecha.setSpacingAfter(20);
        document.add(fecha);

        // 4. Tabla (6 Columnas)
        // ID, Username, Nombre Completo, Rol, Estado, Fecha Registro
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 3f, 4f, 2f, 2f, 3f});

        // Encabezados
        String[] headers = {"ID", "Usuario", "Nombre Personal", "Rol Asignado", "Estado", "Registrado El"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
            cell.setBackgroundColor(Color.DARK_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }

        // 5. Llenado de Datos
        for (com.raidsoft.model.Usuario u : usuarios) {
            // ID
            PdfPCell celdaId = new PdfPCell(new Phrase(String.valueOf(u.getIdUsuario()), fontData));
            celdaId.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(celdaId);

            // Username
            table.addCell(new Phrase(u.getUsername(), fontData));

            // Nombre Completo (Desde Perfil, manejando nulos)
            String nombreCompleto = "Sin Perfil";
            if (u.getPerfil() != null) {
                String n = u.getPerfil().getNombre() != null ? u.getPerfil().getNombre() : "";
                String a = u.getPerfil().getApellido() != null ? u.getPerfil().getApellido() : "";
                nombreCompleto = (n + " " + a).trim();
                if (nombreCompleto.isEmpty()) {
                    nombreCompleto = "-";
                }
            }
            table.addCell(new Phrase(nombreCompleto, fontData));

            // Rol
            PdfPCell celdaRol = new PdfPCell(new Phrase(u.getRol().name(), fontData));
            celdaRol.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(celdaRol);

            // Estado (Texto + Color visual si está inactivo)
            String textoEstado = Boolean.TRUE.equals(u.getEstado()) ? "ACTIVO" : "INACTIVO";
            PdfPCell celdaEstado = new PdfPCell(new Phrase(textoEstado, fontData));
            celdaEstado.setHorizontalAlignment(Element.ALIGN_CENTER);
            if (!Boolean.TRUE.equals(u.getEstado())) {
                celdaEstado.setBackgroundColor(new Color(255, 230, 230)); // Rojo suave para inactivos
                celdaEstado.setBorderColor(Color.RED);
            }
            table.addCell(celdaEstado);

            // Fecha Creación
            String fechaRegistro = "-";
            if (u.getCreatedAt() != null) {
                fechaRegistro = u.getCreatedAt().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
            PdfPCell celdaFecha = new PdfPCell(new Phrase(fechaRegistro, fontData));
            celdaFecha.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(celdaFecha);
        }

        document.add(table);

        // Resumen simple al pie
        Paragraph total = new Paragraph("\nTotal de Usuarios registrados: " + usuarios.size(), fontData);
        document.add(total);

        document.close();
    }

    // Reportes de reabastecimiento
    public void generarReporteReabastecimiento(HttpServletResponse response, List<Producto> productos) throws IOException {
        // 1. Configuración (Vertical esta vez, es una lista tipo checklist)
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // 2. Fuentes
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.GRAY);
        Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Font fontData = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font fontDataBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);

        // 3. Encabezado
        Paragraph titulo = new Paragraph("Orden de Reabastecimiento Sugerida", fontTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("Productos con Stock Crítico o Bajo", fontSubtitulo);
        subtitulo.setAlignment(Paragraph.ALIGN_CENTER);
        subtitulo.setSpacingAfter(10);
        document.add(subtitulo);

        Paragraph fecha = new Paragraph("Fecha de Corte: "
                + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), fontData);
        fecha.setAlignment(Paragraph.ALIGN_RIGHT);
        fecha.setSpacingAfter(20);
        document.add(fecha);

        // 4. Tabla (6 Columnas)
        // Código, Producto, Costo Unit., Stock Actual, Stock Mín, Cantidad a Pedir (Espacio)
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 5f, 2.5f, 2f, 2f, 3f});

        // Encabezados
        String[] headers = {"Código", "Producto", "Costo Unit.", "Actual", "Mínimo", "A Pedir"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, fontHeader));
            cell.setBackgroundColor(new Color(52, 58, 64)); // Gris oscuro casi negro
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }

        // 5. Llenado de Datos
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.US);

        if (productos.isEmpty()) {
            PdfPCell emptyCell = new PdfPCell(new Phrase("¡Excelente! No hay productos con stock bajo.", fontData));
            emptyCell.setColspan(6);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            emptyCell.setPadding(20);
            table.addCell(emptyCell);
        } else {
            for (Producto p : productos) {
                // Código
                table.addCell(new Phrase(p.getCodigoBarras(), fontData));

                // Nombre
                table.addCell(new Phrase(p.getNombre(), fontData));

                // Precio Compra (Costo)
                PdfPCell cellCosto = new PdfPCell(new Phrase(currency.format(p.getPrecioCompra()), fontData));
                cellCosto.setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(cellCosto);

                // Stock Actual (En Rojo y Negrita porque es crítico)
                PdfPCell cellStock = new PdfPCell(new Phrase(String.valueOf(p.getStock()), fontDataBold));
                cellStock.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellStock.setBackgroundColor(new Color(255, 235, 238)); // Rojo muy suave de fondo
                cellStock.setBorderColor(Color.RED); // Borde rojo
                table.addCell(cellStock);

                // Stock Mínimo
                PdfPCell cellMin = new PdfPCell(new Phrase(String.valueOf(p.getStockMinimo()), fontData));
                cellMin.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cellMin);

                // Columna "A Pedir" (Vacía para llenar a mano o calculada)
                // Calculamos una sugerencia: Llegar al Mínimo + 5 unidades (ejemplo) o dejar vacío.
                // Dejaremos un campo visualmente vacío (con guiones bajos) para escritura manual.
                PdfPCell cellPedir = new PdfPCell(new Phrase("_______", fontData));
                cellPedir.setHorizontalAlignment(Element.ALIGN_CENTER);
                cellPedir.setVerticalAlignment(Element.ALIGN_BOTTOM);
                table.addCell(cellPedir);
            }
        }

        document.add(table);

        // 6. Pie de página con instrucciones
        Paragraph notas = new Paragraph("\nNota: La columna 'A Pedir' debe ser completada por el encargado de compras.\n"
                + "Los productos listados tienen un stock igual o inferior al mínimo configurado.", fontData);
        notas.setSpacingBefore(20);
        document.add(notas);

        document.close();
    }
}
