package com.juntavecinos.movimiento.dto;

import java.time.LocalDate;

import com.juntavecinos.movimiento.model.TipoMovimiento;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** reservaId es opcional: si viene, el movimiento es un pago informativo de esa reserva. */
public record MovimientoRequest(
        @NotNull(message = "El tipo es obligatorio") TipoMovimiento tipo,
        @NotNull(message = "Selecciona una categoría") Long categoriaId,
        @NotBlank(message = "La descripción es obligatoria")
        @Size(min = 5, max = 255, message = "La descripción debe tener entre 5 y 255 caracteres") String descripcion,
        @NotNull(message = "El monto es obligatorio")
        @Min(value = 1, message = "El monto debe ser mayor a 0")
        @Max(value = 10_000_000, message = "El monto es demasiado alto") Integer monto,
        @NotNull(message = "Selecciona una fecha") LocalDate fecha,
        Long reservaId,
        @Size(max = 30, message = "El medio de pago es demasiado largo") String medioPago,
        @Size(max = 100, message = "La referencia es demasiado larga") String referencia) {
}
