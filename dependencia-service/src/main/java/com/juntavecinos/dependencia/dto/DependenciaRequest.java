package com.juntavecinos.dependencia.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DependenciaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres") String nombre,
        @Size(max = 255, message = "La descripción es demasiado larga") String descripcion,
        @Size(max = 120, message = "La ubicación es demasiado larga") String ubicacion,
        @NotNull(message = "La capacidad es obligatoria")
        @Min(value = 1, message = "La capacidad debe ser al menos 1")
        @Max(value = 5000, message = "La capacidad es demasiado alta") Integer capacidad) {
}
