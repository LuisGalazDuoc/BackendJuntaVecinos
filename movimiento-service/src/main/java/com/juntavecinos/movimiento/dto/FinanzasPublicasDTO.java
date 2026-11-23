package com.juntavecinos.movimiento.dto;

import java.util.List;

/** resumen es null si la directiva no lo autorizo. */
public record FinanzasPublicasDTO(boolean mostrarResumen, boolean mostrarMovimientos, ResumenDTO resumen,
        List<MovimientoPublicoDTO> movimientos) {
}
