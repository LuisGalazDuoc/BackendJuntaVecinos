package com.juntavecinos.movimiento.dto;

import com.juntavecinos.movimiento.model.TipoMovimiento;

public record TotalCategoriaDTO(TipoMovimiento tipo, String categoria, long total, int cantidad) {
}
