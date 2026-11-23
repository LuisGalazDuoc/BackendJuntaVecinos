package com.juntavecinos.reserva.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Respuesta de dependencia-service. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DependenciaDTO(Long id, String nombre, Integer capacidad, boolean activa) {
}
