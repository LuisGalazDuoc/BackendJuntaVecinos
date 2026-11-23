package com.juntavecinos.movimiento.dto;

import java.time.LocalDate;
import java.util.List;

public record InformeDTO(LocalDate desde, LocalDate hasta, ResumenDTO resumen, List<TotalCategoriaDTO> categorias,
        List<MovimientoDTO> movimientos) {
}
