package com.raidsoft.dto;

import java.math.BigDecimal;

public interface VentaVendedorDTO {
    String getUsername();
    String getNombreCompleto();
    Long getCantidadVentas();
    BigDecimal getTotalVendido();
}
