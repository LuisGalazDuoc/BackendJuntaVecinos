package com.juntavecinos.reserva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ObservacionRequest(
        @NotBlank(message = "La observación es obligatoria")
        @Size(max = 500, message = "La observación es demasiado larga") String observacion) {
}
