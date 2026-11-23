package com.juntavecinos.movimiento.dto;

import com.juntavecinos.movimiento.model.TipoMovimiento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 60, message = "El nombre debe tener entre 3 y 60 caracteres") String nombre,
        @NotNull(message = "El tipo es obligatorio") TipoMovimiento tipo,
        @Size(max = 255, message = "La descripción es demasiado larga") String descripcion,
        @NotNull(message = "Debe indicar si está activa") Boolean activa) {
}
