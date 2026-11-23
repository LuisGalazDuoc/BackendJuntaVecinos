package com.juntavecinos.movimiento.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Respuesta de reserva-service. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReservaDTO(Long id, Long usuarioId, String dependencia, String estado) {
}
