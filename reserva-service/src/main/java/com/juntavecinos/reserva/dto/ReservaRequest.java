package com.juntavecinos.reserva.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Mismas reglas de formulario de la app. El usuario se obtiene del token, no de esta solicitud. */
public record ReservaRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        @Pattern(regexp = "[\\p{L} ]+", message = "El nombre solo permite letras y espacios") String solicitante,
        @NotNull(message = "El teléfono es obligatorio")
        @Pattern(regexp = "9\\d{8}", message = "El teléfono debe tener 9 dígitos y comenzar con 9") String telefono,
        @NotNull(message = "Selecciona una dependencia") Long dependenciaId,
        @NotNull(message = "Selecciona una fecha") LocalDate fecha,
        @NotNull(message = "Selecciona la hora de inicio") @JsonFormat(pattern = "HH:mm") LocalTime horaInicio,
        @NotNull(message = "Selecciona la hora de término") @JsonFormat(pattern = "HH:mm") LocalTime horaFin,
        @NotNull(message = "Indica la cantidad de personas")
        @Min(value = 1, message = "Debe ser al menos 1 persona") Integer personas,
        @NotBlank(message = "Indica el motivo del arriendo")
        @Size(min = 5, max = 255, message = "El motivo debe tener entre 5 y 255 caracteres") String motivo,
        boolean usaCocina,
        @NotNull(message = "El monto es obligatorio")
        @Min(value = 1, message = "El monto debe ser mayor a 0")
        @Max(value = 10_000_000, message = "El monto es demasiado alto") Integer monto,
        @AssertTrue(message = "Debes aceptar el reglamento de uso") boolean aceptaReglamento) {
}
