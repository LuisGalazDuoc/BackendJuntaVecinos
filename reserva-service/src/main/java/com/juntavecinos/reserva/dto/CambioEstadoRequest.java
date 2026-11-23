package com.juntavecinos.reserva.dto;

import com.juntavecinos.reserva.model.EstadoReserva;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CambioEstadoRequest(
        @NotNull(message = "El estado es obligatorio") EstadoReserva estado,
        @Size(max = 500, message = "La observación es demasiado larga") String observacion) {
}
