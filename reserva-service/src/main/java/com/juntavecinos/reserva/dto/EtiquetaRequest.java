package com.juntavecinos.reserva.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EtiquetaRequest(
        @NotBlank(message = "El nombre visible es obligatorio")
        @Size(min = 3, max = 40, message = "El nombre visible debe tener entre 3 y 40 caracteres") String etiqueta) {
}
