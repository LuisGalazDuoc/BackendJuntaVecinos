package com.juntavecinos.comprobante.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Respuesta de movimiento-service. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MovimientoDTO(Long id, String estado) {
}
