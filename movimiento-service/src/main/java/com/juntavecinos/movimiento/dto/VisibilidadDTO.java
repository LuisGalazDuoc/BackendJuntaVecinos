package com.juntavecinos.movimiento.dto;

import com.juntavecinos.movimiento.model.Visibilidad;

import jakarta.validation.constraints.NotNull;

public record VisibilidadDTO(
        @NotNull(message = "Debe indicar si se muestra el resumen") Boolean mostrarResumen,
        @NotNull(message = "Debe indicar si se muestran los movimientos") Boolean mostrarMovimientos) {

    public static VisibilidadDTO fromModel(Visibilidad v) {
        return new VisibilidadDTO(v.isMostrarResumen(), v.isMostrarMovimientos());
    }
}
