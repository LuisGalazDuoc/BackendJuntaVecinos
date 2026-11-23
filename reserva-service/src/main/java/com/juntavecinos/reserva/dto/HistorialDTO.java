package com.juntavecinos.reserva.dto;

import java.time.LocalDateTime;

import com.juntavecinos.reserva.model.EstadoReserva;
import com.juntavecinos.reserva.model.HistorialReserva;

public record HistorialDTO(EstadoReserva estadoAnterior, EstadoReserva estadoNuevo, String observacion,
        LocalDateTime fechaCambio) {

    public static HistorialDTO fromModel(HistorialReserva h) {
        return new HistorialDTO(h.getEstadoAnterior(), h.getEstadoNuevo(), h.getObservacion(), h.getFechaCambio());
    }
}
