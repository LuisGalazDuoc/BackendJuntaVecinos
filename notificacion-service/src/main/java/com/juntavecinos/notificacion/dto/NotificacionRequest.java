package com.juntavecinos.notificacion.dto;

import com.juntavecinos.notificacion.model.TipoNotificacion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificacionRequest(Long usuarioId, String rolDestino,
        @NotNull(message = "El tipo es obligatorio") TipoNotificacion tipo,
        @NotBlank(message = "El título es obligatorio") @Size(max = 120) String titulo,
        @NotBlank(message = "El mensaje es obligatorio") @Size(max = 1000) String mensaje,
        Long reservaId, Long movimientoId) {
}
